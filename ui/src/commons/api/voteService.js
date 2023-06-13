import {
    DEFAULT_CONTENT_TYPE_HEADERS,
    doRequest,
    METHODS,
} from '../handlers/httpHandler';
import { CAST_VOTE_URL } from '../constants/appConstants';

const castAVoteWithDigitalSignature = async (jsonRequest) => {
    return await doRequest(
        METHODS.POST,
        `${CAST_VOTE_URL}`,
        { ...DEFAULT_CONTENT_TYPE_HEADERS },
        JSON.stringify(jsonRequest),
        true
    );
};

export const eVoteService = {
    castAVoteWithDigitalSignature: castAVoteWithDigitalSignature
};
