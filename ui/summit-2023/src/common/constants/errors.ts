import {capitalizeFirstLetter} from '../../utils/utils';

const ERRORS = {
  VOTE_CANNOT_BE_CHANGED: 'Vote cannot be changed',
};

const parseError = (errorMessage: string) => {
  return Object.keys(ERRORS).includes(errorMessage) ? ERRORS[errorMessage] : capitalizeFirstLetter(errorMessage)
}

export { ERRORS, parseError };
