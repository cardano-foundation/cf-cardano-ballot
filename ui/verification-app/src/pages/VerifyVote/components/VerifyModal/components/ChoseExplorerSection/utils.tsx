import { styled } from '@mui/material/styles';
import React from 'react';

const Img = styled('img')({
  height: '23px',
  width: '23px',
  objectFit: 'cover',
  margin: 'auto',
  display: 'block',
  maxWidth: '100%',
  maxHeight: '100%',
  marginRight: '10px',
});

export const EXPLORERS = [
  {
    name: 'Explorer',
    label: 'Explorer',
    url: 'https://explorer.cardano.org/en/transaction?id=',
    icon: <Img src="/static/ie.png" />,
  },
  {
    name: 'Cardanoscan',
    label: 'Cardanoscan',
    url: 'https://cardanoscan.io/transaction/',
    icon: <Img src="/static/cardanoscan.png" />,
  },
  {
    name: 'Cexplorer',
    label: 'Cexplorer',
    url: 'https://cexplorer.io/tx/',
    icon: <Img src="/static/cexplorer.png" />,
  },
];
