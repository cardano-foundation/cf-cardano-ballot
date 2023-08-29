import React, { useState, useEffect, useCallback } from 'react';
import { useParams } from 'react-router-dom';
import { useTheme, useMediaQuery, Typography, IconButton, Grid, Card, CardContent, Button, Box } from '@mui/material';
import ViewModuleIcon from '@mui/icons-material/ViewModule';
import ViewListIcon from '@mui/icons-material/ViewList';
import AccountBalanceWalletIcon from '@mui/icons-material/AccountBalanceWallet';

import './Nominees.scss';

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
];

const Nominees = () => {
  const { id } = useParams();
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('sm'));
  const isLarger = useMediaQuery(theme.breakpoints.up('xxl'));

  const [listView, setListView] = useState<'grid' | 'list'>('grid');
  const [selectedViewType, setSelectedViewType] = useState<'grid' | 'list'>('grid');
  const [isVisible, setIsVisible] = useState(true);

  useEffect(() => {
    if (isMobile) {
      setListView('list');
      setSelectedViewType('list');
    }
  }, [isMobile]);

  const handleListView = (viewType: 'grid' | 'list') => {
    setSelectedViewType(viewType);
    setIsVisible(false);
  };

  useEffect(() => {
    if (!isVisible) {
      const timer = setTimeout(() => {
        setListView(selectedViewType);
        setIsVisible(true);
      }, 600);

      return () => clearTimeout(timer);
    }
  }, [isVisible]);

  return (
    <>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 20 }}>
        <Typography
          className="nominees-title"
          variant="h4"
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
        variant="body1"
        gutterBottom
      >
        To commemorate the special commitment and work of a Cardano Ambassador.
      </Typography>

      <Grid
        container
        spacing={3}
      >
        {dummyData.map((item) => (
          <Grid
            item
            xs={!isMobile && listView === 'grid' ? 4 : 12}
            key={item.id}
          >
            <Card
              className={`nominee-card ${isVisible ? 'fade-in' : 'fade-out'}`}
              style={{
                padding: '8px',
                width: listView === 'list' ? '100%' : '414px',
                height: !isMobile && listView === 'list' ? '220px' : '370px',
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
                    xs={!isMobile && listView === 'list' ? 9 : 12}
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
                      xs={3}
                    >
                      <Button
                        className="connect-wallet-button"
                        style={{ width: '214px', marginLeft: '30%' }}
                      >
                        <AccountBalanceWalletIcon /> Connect Wallet
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
                {!isMobile && listView === 'grid' ? (
                  <Button
                    className="connect-wallet-button"
                    fullWidth
                  >
                    <AccountBalanceWalletIcon /> Connect Wallet
                  </Button>
                ) : null}
              </CardContent>
            </Card>
          </Grid>
        ))}
      </Grid>
    </>
  );
};

export { Nominees };
