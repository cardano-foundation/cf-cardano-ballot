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
import './Proposals.scss';
import { eventBus } from '../../utils/EventBus';
import { useCardano } from '@cardano-foundation/cardano-connect-with-wallet';
import CloseIcon from '@mui/icons-material/Close';
import xIcon from '../../common/resources/images/x-icon.svg';
import linkedinIcon from '../../common/resources/images/linkedin-icon.svg';
import { ROUTES } from '../../routes';
import { useSelector } from 'react-redux';
import { RootState } from '../../store';
import { ProposalPresentation } from '../../types/voting-ledger-follower-types';
export interface CategoryDescriptions {
  id: string;
  desc: string;
  proposals: {
    id: string;
    desc: string;
  }[];
}

const Proposals = () => {
  const { categoryId } = useParams();
  const navigate = useNavigate();
  const eventCache = useSelector((state: RootState) => state.user.event);
  const categories = eventCache?.categories;
  const categories_ids = categories?.map((e) => e.id);
  if (categoryId && !categories_ids?.includes(categoryId)) navigate(ROUTES.NOT_FOUND);
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('sm'));

  const [listView, setListView] = useState<'grid' | 'list'>('grid');
  const [isVisible, setIsVisible] = useState(true);
  const [drawerOpen, setDrawerOpen] = useState(false);
  const [proposals, setProposals] = useState<ProposalPresentation[]>([]);

  const { isConnected } = useCardano();

  useEffect(() => {
    if (isMobile) {
      setListView('list');
    }
  }, [isMobile]);

  const loadProposals = () => {
    if (categoryId) {
      categories?.map((category) => {
        if (category.id === categoryId) {
          setProposals(category?.proposals || []);
        }
      });
    } else {
      navigate(ROUTES.NOT_FOUND);
    }
  };

  useEffect(() => {
    loadProposals();
  }, []);

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
          className="proposals-title"
          variant="h4"
        >
          {categoryId}
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
        className="proposals-description"
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
        {proposals.map((proposal) => (
          <Grid
            item
            xs={!isMobile && listView === 'grid' ? 4 : 12}
            key={proposal.id}
          >
            <Fade in={isVisible}>
              <Card
                className={'proposal-card'}
                style={{
                  padding: '8px',
                  width: listView === 'list' ? '100%' : '414px',
                  height: !isMobile && listView === 'list' ? 'auto' : isMobile ? '440px' : '390px',
                }}
              >
                <CardContent>
                  <Typography
                    className="proposal-title"
                    variant="h2"
                  >
                    {proposal.name}
                  </Typography>
                  <Grid container>
                    <Grid
                      item
                      xs={!isMobile && listView === 'list' ? 10 : 12}
                    >
                      <Typography
                        className="proposal-description"
                        variant="body2"
                      >
                        {proposal.presentationName}
                      </Typography>
                    </Grid>
                    {!isMobile && listView === 'list' ? (
                      <Grid
                        item
                        xs={2}
                      >
                        <Button
                          className={`${isConnected ? 'vote-proposal-button' : 'connect-wallet-button'}`}
                          style={{ width: 'auto' }}
                          onClick={() => (isConnected ? handleActionButton() : openConnectWalletModal())}
                        >
                          {isConnected ? (
                            <>Vote for proposal</>
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
                      className={`${isConnected ? 'vote-proposal-button' : 'connect-wallet-button'}`}
                      fullWidth
                      onClick={() => (isConnected ? handleActionButton() : openConnectWalletModal())}
                    >
                      {isConnected ? (
                        <>Vote for proposal</>
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
            className="proposal-slide-title"
          >
            proposal
          </Typography>

          <Typography
            variant="subtitle1"
            gutterBottom
            className="proposal-slide-subtitle"
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
                className="proposal-social-button"
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
                className="proposal-social-button"
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
            className="proposal-slide-description"
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

export { Proposals };
