import TAndC from './resources/CF_T&C.pdf';
import Privacy from './resources/CF_Privacy_Policy.pdf';
import { env } from '../../env';

export const getFooterLinks = () =>
  [
    { 'data-testid': 'f-a-q', href: env.FAQ_URL, text: 'Guides' },
    { 'data-testid': 't-and-c', type: 'application/pdf', href: TAndC, text: 'Terms & Conditions' },
    { 'data-testid': 'privacy', type: 'application/pdf', href: Privacy, text: 'Privacy' },
  ].filter(({ href }) => !!href);
