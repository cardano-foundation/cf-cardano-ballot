export enum HttpMethods {
  GET = 'GET',
  POST = 'POST',
  PUT = 'PUT',
  DELETE = 'DELETE',
  PATCH = 'PATCH',
}

export enum MediaTypes {
  APPLICATION_JSON = 'application/json',
  APPLICATION_JSON_UTF8 = 'application/json;charset=UTF-8',
  APPLICATION_JSON_UTF8_FORM_URLENCODED = 'application/x-www-form-urlencoded',
  MULTIPART_FORM_DATA = 'multipart/form-data',
  APPLICATION_OCTET_STREAM = 'application/octet-stream',
}

export enum Headers {
  CONTENT_TYPE = 'Content-Type',
  CONTENT_LENGTH = 'Content-Length',
  CARDANO_BALLOT_TRACE_ID = 'X-Cardano-Ballot-Trace-ID',
  AUTHORIZATION = 'Authorization',
  ACCEPT = 'Accept',
}

type contentTypeHeaders = Record<Headers, MediaTypes>;

export const DEFAULT_CONTENT_TYPE_HEADERS: Partial<contentTypeHeaders> = {
  [Headers.CONTENT_TYPE]: MediaTypes.APPLICATION_JSON_UTF8,
};

export enum ERROR_CODES {
  TOKEN_EXPIRED = 'TOKEN_EXPIRED',
}

export class HttpError extends Error {
  code: number;
  url: string;
  message: string;
  constructor(statusCodeError: number, url: string, message: string) {
    super('Failed request [' + url + ']\n' + message);

    this.code = statusCodeError;
    this.url = url;
    this.message = message;
  }
}

const DEFAULT_REQUEST_PARAMETERS = {};

type AnuthorizedResponse = {
  error_description?: string;
  errors?: Array<{ errorCode: string }>;
  error?: string;
  fault?: { faultstring?: string };
  message?: string;
  Error?: string | Error;
} & Response;
async function getErrorMessage(response: AnuthorizedResponse): Promise<string> {
  if (response.error) {
    return response.error_description ? response.error_description : response.error;
  } else if (response.errors && response.errors.length > 0) {
    const messages = [];
    if (response && response.errors && response.errors.length > 0) {
      for (const e of response.errors) {
        messages.push(e.errorCode);
      }
    }
    return messages.toString();
  } else if (response.fault && response.fault.faultstring) {
    return response.fault.faultstring;
  } else if (response.message) {
    try {
      const errors = JSON.parse(response.message);
      const messages = [];
      for (const e of errors) {
        messages.push(e.errorCode + ': ' + e.message);
      }
      return messages.toString();
    } catch (e) {
      return response.message;
    }
  } else if (response.Error) {
    return typeof response.Error === 'object' ? response.Error.message : response.Error;
  } else {
    return '' + response;
  }
}

export function responseErrorsHandler() {
  return {
    parse(errors: Errors['errors']) {
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

type NoContentResponse = { status: number; message: string };
type Errors = { errors: Array<{ errorCode: string }> } & Omit<Response, 'errors'>;

export function responseHandlerDelegate<T>() {
  const errorsHandler = responseErrorsHandler();

  return {
    async parse(response: Response | AnuthorizedResponse): Promise<T | NoContentResponse | never> {
      let json!: T & Errors;

      if (response.status === 204) {
        return {
          status: 204,
          message: 'Success',
        };
      }

      try {
        json = await response.json();
      } catch (err) {
        if (response.status !== 200) {
          throw new HttpError(401, response.url, await getErrorMessage(response));
        }
      }

      if (json === undefined && response.status === 200) {
        return {
          status: 200,
          message: 'Success',
        };
      }

      if (typeof json === 'object' && 'errors' in json && json.errors.length >= 1) {
        throw new HttpError(400, response.url, errorsHandler.parse(json.errors));
      } else {
        return json;
      }
    },
  };
}

type RequestInit = {
  method: HttpMethods;
  headers: Partial<contentTypeHeaders>;
  body?: string;
};

async function executeRequest<T>(
  requestUri: string,
  method: HttpMethods,
  headers: Partial<contentTypeHeaders>,
  body?: string
) {
  const request: RequestInit = {
    method: method || HttpMethods.GET,
    headers: headers || {},
    ...DEFAULT_REQUEST_PARAMETERS,
  };

  if (body && (method === HttpMethods.POST || method === HttpMethods.PUT || method === HttpMethods.PATCH)) {
    request['body'] = body;
  }

  const responseHandler = responseHandlerDelegate<T>();
  return responseHandler.parse(await fetch(requestUri, request));
}

async function execute<T>(
  url: string,
  method: HttpMethods,
  headers: Partial<contentTypeHeaders>,
  body?: string,
  useAuth?: boolean
) {
  // TODO: useAuth is not used in executeRequest?
  console.log(useAuth);
  return await executeRequest<T>(url, method, headers, body).catch((err) => {
    throw err;
  });
}

export function get<T>(url: string, headers: Partial<contentTypeHeaders>, useAuth?: boolean) {
  return execute<T>(url, HttpMethods.GET, headers, undefined, useAuth);
}

export function post<T>(url: string, headers: Partial<contentTypeHeaders>, body?: string, useAuth?: boolean) {
  return execute<T>(url, HttpMethods.POST, headers, body, useAuth);
}

export function remove<T>(url: string, headers: Partial<contentTypeHeaders>, useAuth?: boolean) {
  return execute<T>(url, HttpMethods.DELETE, headers, undefined, useAuth);
}

export function put<T>(url: string, headers: Partial<contentTypeHeaders>, body?: string, useAuth?: boolean) {
  return execute<T>(url, HttpMethods.PUT, headers, body, useAuth);
}

export function patch<T>(url: string, headers: Partial<contentTypeHeaders>, body?: string, useAuth?: boolean) {
  return execute<T>(url, HttpMethods.PATCH, headers, body, useAuth);
}

export const doRequest = async <T>(
  method: HttpMethods,
  url: string,
  headers: Partial<contentTypeHeaders>,
  body?: string,
  useAuth = false
) => {
  const allHeaders = headers || DEFAULT_CONTENT_TYPE_HEADERS;

  if (method === HttpMethods.POST) {
    return await post<T>(url, allHeaders, body, useAuth);
  } else if (method === HttpMethods.PUT) {
    return await put<T>(url, allHeaders, body, useAuth);
  } else if (method === HttpMethods.DELETE) {
    return await remove<T>(url, allHeaders, useAuth);
  } else if (method === HttpMethods.PATCH) {
    return await patch<T>(url, allHeaders, body, useAuth);
  } else {
    return await get<T>(url, allHeaders, useAuth);
  }
};
