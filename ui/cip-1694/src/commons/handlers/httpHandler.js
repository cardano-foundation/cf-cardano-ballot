export async function doRequest(method, url, headers, body, useAuth= false) {
    const allHeaders = headers || DEFAULT_CONTENT_TYPE_HEADERS;

    if (method === METHODS.POST) {
        return await post(url, allHeaders, body, useAuth);
    } else if (method === METHODS.PUT) {
        return await put(url, allHeaders, body, useAuth);
    } else if (method === METHODS.DELETE) {
        return await remove(url, allHeaders,  useAuth);
    } else if (method === METHODS.PATCH) {
        return await patch(url, allHeaders, body, useAuth);
    } else {
        return await get(url, allHeaders, useAuth);
    }
}

export const HEADERS = {
    CONTENT_TYPE: 'Content-Type',
    CONTENT_LENGTH: 'Content-Length',
    CARDANO_BALLOT_TRACE_ID: 'X-Cardano-Ballot-Trace-ID',
    AUTHORIZATION: 'Authorization',
    ACCEPT: 'Accept',
};

export const METHODS = {
    GET: 'GET',
    POST: 'POST',
    PUT: 'PUT',
    DELETE: 'DELETE',
    PATCH: 'PATCH',
};

export const ERROR_CODE = {
    TOKEN_EXPIRED: "TOKEN_EXPIRED"
}

export const MEDIA_TYPES = {
    APPLICATION_JSON: 'application/json',
    APPLICATION_JSON_UTF8: 'application/json;charset=UTF-8',
    APPLICATION_JSON_UTF8_FORM_URLENCODED: 'application/x-www-form-urlencoded',
    MULTIPART_FORM_DATA: 'multipart/form-data',
    APPLICATION_OCTET_STREAM: 'application/octet-stream',
};

export const DEFAULT_CONTENT_TYPE_HEADERS = {
    [HEADERS.CONTENT_TYPE]: MEDIA_TYPES.APPLICATION_JSON_UTF8,
};

export function get(url, headers, useAuth) {
    return execute(url, METHODS.GET, headers, null, useAuth);
}

export function post(url, headers, body, useAuth) {
    return execute(url, METHODS.POST, headers, body, useAuth);
}

export function remove(url, headers, useAuth) {
    return execute(url, METHODS.DELETE, headers, null, useAuth);
}

export function put(url, headers, body, useAuth) {
    return execute(url, METHODS.PUT, headers, body, useAuth);
}

export function patch(url, headers, body, useAuth) {
    return execute(url, METHODS.PATCH, headers, body, useAuth);
}

export class HttpError extends Error {
    constructor(statusCodeError, url, stacktrace) {
        super('Failed request [' + url + ']\n' + stacktrace);

        this.code = statusCodeError;
        this.url = url;
        this.stacktrace = stacktrace;
    }
}

let DEFAULT_REQUEST_PARAMETERS = {};

async function execute(url, method, headers, body, useAuth) {
    return await executeRequest(url, method, headers, body, useAuth).catch((err) => {
        throw err;
    });
}

async function executeRequest(requestUri, method, headers, body, useAuth) {

    const request = {
        method: method || METHODS.GET,
        headers: headers || {},
        ...DEFAULT_REQUEST_PARAMETERS,
    };

    if (
        body &&
        (method === METHODS.POST ||
            method === METHODS.PUT ||
            method === METHODS.PATCH)
    ) {
        request['body'] = body;
    }

    return responseHandler.parse(await fetch(requestUri, request));
}

export function ResponseHandlerDelegate() {
    const errorsHandler = ResponseErrorsHandler();

    return {
        async parse(response) {
            let json;
            try {
                json = await response.json();
                if (response.status === 204) {
                    return {
                        status: response.status,
                        message: 'Success',
                    };
                }
            } catch (err) {
                if (json === undefined && response.status === 200) {
                    return {
                        status: response.status,
                        message: 'Success',
                    };
                } else {
                    throw new HttpError(
                        401,
                        response.url,
                        await getErrorMessage(response)
                    );
                }
            }
            if (json && json.errors && json.errors.length >= 1) {
                throw new HttpError(
                    400,
                    response.url,
                    errorsHandler.parse(json.errors)
                );
            } else {
                return json;
            }
        },
    };
}

let responseHandler = ResponseHandlerDelegate();

export function ResponseErrorsHandler() {
    return {
        parse(errors) {
            return (errors || [])
                .map((error) => {
                    if (error.errorCode) {
                        return error.errorCode;
                    }
                })
                .toString();
        },
    };
}

async function getErrorMessage(response) {
    if (response.error) {
        return response.error_description
            ? response.error_description
            : response.error;
    } else if (response.errors && response.errors.length > 0) {
        let messages = [];
        if (response && response.errors && response.errors.length > 0) {
            for (let e of response.errors) {
                messages.push(e.errorCode);
            }
        }
        return messages.toString();
    } else if (response.fault && response.fault.faultstring) {
        return response.fault.faultstring;
    } else if (response.message) {
        try {
            const errors = JSON.parse(response.message);
            let messages = [];
            for (let e of errors) {
                messages.push(e.errorCode + ': ' + e.message);
            }
            return messages.toString();
        } catch (e) {
            return response.message;
        }
    } else if (response.Error) {
        return response.Error;
    } else {
        return '' + response;
    }
}
