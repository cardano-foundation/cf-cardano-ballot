import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import {
  useTheme,
  useMediaQuery,
  Typography,
  IconButton,
  Grid,
  Card,
  CardContent,
  Button,
  Drawer,
  Container,
} from '@mui/material';
import { useNavigate } from 'react-router-dom';
import ViewModuleIcon from '@mui/icons-material/ViewModule';
import ViewListIcon from '@mui/icons-material/ViewList';
import AccountBalanceWalletIcon from '@mui/icons-material/AccountBalanceWallet';
import { Fade } from '@mui/material';
import './Nominees.scss';
import { eventBus } from '../../utils/EventBus';
import { useCardano } from '@cardano-foundation/cardano-connect-with-wallet';
import CloseIcon from '@mui/icons-material/Close';
import xIcon from '../../common/resources/images/x-icon.svg';
import linkedinIcon from '../../common/resources/images/linkedin-icon.svg';
import nominees from '../../common/resources/data/nominees.json';
import { ROUTES } from '../../routes';
import {useSelector} from 'react-redux';
import {RootState} from '../../store';

const Nominees = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const eventCache = useSelector((state: RootState) => state.user.event);
  const categories_ids = eventCache.categories.map(e => e.id);
  if (!categories_ids.includes(id)) navigate(ROUTES.NOT_FOUND);

  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('sm'));

  const [listView, setListView] = useState<'grid' | 'list'>('grid');
  const [isVisible, setIsVisible] = useState(true);
  const [drawerOpen, setDrawerOpen] = useState(false);

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

  const handleActionButton = () => {
    // TODO:
  };

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
        style={{ width: isMobile ? '360px' : '414px' }}
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
        {nominees.map((item) => (
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
                          className={`${isConnected ? 'vote-nominee-button' : 'connect-wallet-button'}`}
                          style={{ width: 'auto' }}
                          onClick={() => (isConnected ? handleActionButton() : openConnectWalletModal())}
                        >
                          {isConnected ? (
                            <>Vote for nominee</>
                          ) : (
                            <>
                              <AccountBalanceWalletIcon /> Connect Wallet
                            </>
                          )}
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
                    onClick={() => setDrawerOpen(true)}
                  >
                    Read more
                  </Button>
                  {isMobile || listView === 'grid' ? (
                    <Button
                      className={`${isConnected ? 'vote-nominee-button' : 'connect-wallet-button'}`}
                      fullWidth
                      onClick={() => (isConnected ? handleActionButton() : openConnectWalletModal())}
                    >
                      {isConnected ? (
                        <>Vote for nominee</>
                      ) : (
                        <>
                          <AccountBalanceWalletIcon /> Connect Wallet
                        </>
                      )}
                    </Button>
                  ) : null}
                </CardContent>
              </Card>
            </Fade>
          </Grid>
        ))}
      </Grid>
      <Drawer
        anchor="right"
        open={drawerOpen}
        onClose={() => setDrawerOpen(false)}
      >
        <Grid
          container
          p={1}
        >
          <Grid
            item
            xs={11}
          />
          <Grid
            item
            xs={1}
          >
            <IconButton
              className="closeButton"
              onClick={() => setDrawerOpen(false)}
              aria-label="close"
              style={{ float: 'right' }}
            >
              <CloseIcon />
            </IconButton>
          </Grid>
        </Grid>

        <Container style={{ margin: '5px' }}>
          <Typography
            variant="h5"
            gutterBottom
            className="nominee-slide-title"
          >
            Nominee
          </Typography>

          <Typography
            variant="subtitle1"
            gutterBottom
            className="nominee-slide-subtitle"
          >
            Company Name
          </Typography>

          <Grid
            container
            spacing={1}
            marginTop={1}
            marginBottom={2}
          >
            <Grid item>
              <IconButton
                className="nominee-social-button"
                aria-label="X"
              >
                <img
                  src={xIcon}
                  alt="X"
                  style={{ width: '20px' }}
                />
              </IconButton>
            </Grid>
            <Grid item>
              <IconButton
                className="nominee-social-button"
                aria-label="Linkedin"
              >
                <img
                  src={linkedinIcon}
                  alt="Linkedin"
                  style={{ width: '20px' }}
                />
              </IconButton>
            </Grid>
          </Grid>

          <Typography
            variant="body2"
            paragraph
            style={{ maxWidth: '490px' }}
            className="nominee-slide-description"
          >
            Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et
            dolore magna aliqua. Habitant morbi tristique senectus et netus. In massa tempor nec feugiat nisl pretium
            fusce id. Scelerisque felis imperdiet proin fermentum leo vel orci. Tortor condimentum lacinia quis vel eros
            donec ac. Malesuada bibendum arcu vitae elementum curabitur vitae nunc sed velit. Nunc aliquet bibendum enim
            facilisis gravida neque convallis a. Egestas pretium aenean pharetra magna ac placerat vestibulum. Volutpat
            maecenas volutpat blandit aliquam etiam.
          </Typography>

          <Button
            className="visit-web-button"
            href={'#'}
            fullWidth
          >
            Visit Website
          </Button>
        </Container>
      </Drawer>
    </>
  );
};

export { Nominees };
