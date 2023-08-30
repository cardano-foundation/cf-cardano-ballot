import React, { useState, useEffect, useCallback } from 'react';
import { useParams } from 'react-router-dom';
import { useTheme, useMediaQuery, Typography, IconButton, Grid, Card, CardContent, Button, Box } from '@mui/material';
import ViewModuleIcon from '@mui/icons-material/ViewModule';
import ViewListIcon from '@mui/icons-material/ViewList';
import AccountBalanceWalletIcon from '@mui/icons-material/AccountBalanceWallet';
import { Fade } from '@mui/material';
import './Nominees.scss';
import {eventBus} from '../../utils/EventBus';
import {useCardano} from '@cardano-foundation/cardano-connect-with-wallet';

type CardItem = {
  id: number;
  title: string;
  description: string;
};

const dummyData: CardItem[] = [
  {
    id: 1,
    title: 'Nominee',
    description:
      'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Leo in vitae turpis massa sed elementum tempus. Et tortor consequat id porta nibh venenatis cras sed felis.',
  },
  {
    id: 1,
    title: 'Nominee',
    description:
      'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Leo in vitae turpis massa sed elementum tempus. Et tortor consequat id porta nibh venenatis cras sed felis.',
  },
  {
    id: 1,
    title: 'Nominee',
    description:
      'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Leo in vitae turpis massa sed elementum tempus. Et tortor consequat id porta nibh venenatis cras sed felis.',
  },
  {
    id: 1,
    title: 'Nominee',
    description:
      'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Leo in vitae turpis massa sed elementum tempus. Et tortor consequat id porta nibh venenatis cras sed felis.',
  }
];

const Nominees = () => {
  const { id } = useParams();
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('sm'));

  const [listView, setListView] = useState<'grid' | 'list'>('grid');
  const [isVisible, setIsVisible] = useState(true);

  const { isConnected } = useCardano();

  useEffect(() => {
    if (isMobile) {
      setListView('list');
    }
  }, [isMobile]);

  const handleListView = (viewType: 'grid' | 'list') => {

    if (listView === viewType) return;

    setIsVisible(false);
    setTimeout(() => {
      setListView(viewType);
      setIsVisible(true);
    }, 300);
  };

  const openConnectWalletModal = () => {
    eventBus.publish('openConnectWalletModal');
  };

  return (
    <>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 20 }}>
        <Typography
          className='nominees-title'
          variant='h4'
        >
          {id}
        </Typography>
        {!isMobile && (
          <div>
            <IconButton onClick={() => handleListView('grid')}>
              <ViewModuleIcon />
            </IconButton>
            <IconButton onClick={() => handleListView('list')}>
              <ViewListIcon />
            </IconButton>
          </div>
        )}
      </div>

      <Typography
        className="nominees-description"
        style={{width: isMobile ? '360px' : '414px'}}
        variant="body1"
        gutterBottom
      >
        To commemorate the special commitment and work of a Cardano Ambassador.
      </Typography>

      <Grid
        container
        spacing={3}
        style={{ justifyContent: 'center' }}
      >
        {dummyData.map((item) => (
          <Grid
            item
            xs={!isMobile && listView === 'grid' ? 4 : 12}
            key={item.id}
          >
            <Fade in={isVisible}>
              <Card
                  className={'nominee-card'}
                  style={{
                    padding: '8px',
                    width: listView === 'list' ? '100%' : '414px',
                    height: !isMobile && listView === 'list' ? '220px' : isMobile ? '440px' : '390px',
                  }}
              >
                <CardContent>
                  <Typography
                      className="nominee-title"
                      variant="h5"
                  >
                    {item.title}
                  </Typography>
                  <Grid container>
                    <Grid
                        item
                        xs={!isMobile && listView === 'list' ? 10 : 12}
                    >
                      <Typography
                          className="nominee-description"
                          variant="body2"
                      >
                        {item.description}
                      </Typography>
                    </Grid>
                    {!isMobile && listView === 'list' ? (
                        <Grid
                            item
                            xs={2}
                        >
                          <Button
                              className={`${isConnected ? 'vote-nominee-button' :'connect-wallet-button'}`}
                              style={{ width: 'auto' }}
                              onClick={() => openConnectWalletModal()}
                          >
                            {
                              isConnected ? <>Vote for nominee</> : <><AccountBalanceWalletIcon /> Connect Wallet</>
                            }
                          </Button>
                        </Grid>
                    ) : null}
                  </Grid>

                  <Button
                      className="read-more-button"
                      fullWidth
                      style={{
                        width: !isMobile && listView === 'list' ? '146px' : '98%',
                        marginTop: !isMobile && listView === 'list' ? '15px' : '28px',
                      }}
                  >
                    Read more
                  </Button>
                  {isMobile || listView === 'grid' ? (
                      <Button
                          className={`${isConnected ? 'vote-nominee-button'  :'connect-wallet-button'}`}
                          fullWidth
                          onClick={() => openConnectWalletModal()}
                      >
                        {
                          isConnected ? <>Vote for nominee</> : <><AccountBalanceWalletIcon /> Connect Wallet</>
                        }
                      </Button>
                  ) : null}
                </CardContent>
              </Card>
            </Fade>
          </Grid>
        ))}
      </Grid>
    </>
  );
};

export { Nominees };
