import { LEADERBOARD_URL } from 'common/api/leaderboardService';
import { EVENT_BY_ID_REFERENCE_URL } from 'common/api/referenceDataService';
import { VERIFICATION_URL } from 'common/api/verificationService';
import { BLOCKCHAIN_TIP_URL, CAST_VOTE_URL, VOTE_RECEIPT_URL, VOTING_POWER_URL } from 'common/api/voteService';
import { env } from 'env';
import { rest } from 'msw';
import {
  VoteReceiptMock_Full_MediumAssurance,
  VoteReceiptNotFound,
  accountDataMock,
  chainTipMock,
  eventMock_active,
  voteStats,
} from 'test/mocks';

let receiptSubmitted = false;

export const handlers = [
  rest.get(`${LEADERBOARD_URL}/${env.EVENT_ID}/${env.CATEGORY_ID}`, (req, res, ctx) => {
    return res(ctx.json(voteStats), ctx.delay(150));
  }),
  rest.get(`${EVENT_BY_ID_REFERENCE_URL}/${env.EVENT_ID}`, (req, res, ctx) => {
    return res(ctx.json(eventMock_active), ctx.delay(150));
  }),
  rest.post(`${VERIFICATION_URL}`, (req, res, ctx) => {
    return res(ctx.json(true), ctx.delay(150));
  }),
  rest.post(`${CAST_VOTE_URL}`, (req, res, ctx) => {
    receiptSubmitted = true;
    return res(ctx.json({}), ctx.delay(150));
  }),
  rest.get(`${BLOCKCHAIN_TIP_URL}`, (req, res, ctx) => {
    return res(ctx.json(chainTipMock), ctx.delay(150));
  }),
  rest.post(`${VOTE_RECEIPT_URL}`, (req, res, ctx) => {
    return receiptSubmitted
      ? res(ctx.json(VoteReceiptMock_Full_MediumAssurance), ctx.delay(150))
      : res(ctx.status(404), ctx.json(VoteReceiptNotFound), ctx.delay(150));
  }),
  rest.get(`${VOTING_POWER_URL}/:eventId/:staeAddress`, (req, res, ctx) => {
    return res(ctx.json(accountDataMock), ctx.delay(150));
  }),
];
