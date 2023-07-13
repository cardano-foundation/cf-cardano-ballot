import {
    DEFAULT_CONTENT_TYPE_HEADERS,
    doRequest,
    METHODS,
} from '../handlers/httpHandler';
import { EVENT_BY_ID_REFERENCE_URL, CAST_VOTE_URL, BLOCKCHAIN_TIP_URL, VOTING_POWER_URL } from '../constants/appConstants';

const getEventById = async (eventId) => {
    return await doRequest(
        METHODS.GET,
        `${EVENT_BY_ID_REFERENCE_URL}/${eventId}`,
        {
            ...DEFAULT_CONTENT_TYPE_HEADERS,
        }
    );
};

const castAVoteWithDigitalSignature = async (jsonRequest) => {
    return await doRequest(
        METHODS.POST,
        `${CAST_VOTE_URL}`,
        { ...DEFAULT_CONTENT_TYPE_HEADERS },
        JSON.stringify(jsonRequest),
        false
    );
};

const getSlotNumber = async () => {
    return await doRequest(
        METHODS.GET,
        `${BLOCKCHAIN_TIP_URL}`,
        { ...DEFAULT_CONTENT_TYPE_HEADERS }
    );
};

const getVotingPower = async (eventId, stakeAddress) => {
    return await doRequest(
        METHODS.GET,
        `${VOTING_POWER_URL}/${eventId}/${stakeAddress}`,
        { ...DEFAULT_CONTENT_TYPE_HEADERS }
    );
}

export const voteService = {
    getEventById: getEventById,
    castAVoteWithDigitalSignature: castAVoteWithDigitalSignature,
    getSlotNumber: getSlotNumber,
    getVotingPower: getVotingPower
};
