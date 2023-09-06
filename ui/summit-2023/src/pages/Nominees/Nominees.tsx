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
  Button
} from '@mui/material';
import { useNavigate } from 'react-router-dom';
import ViewModuleIcon from '@mui/icons-material/ViewModule';
import ViewListIcon from '@mui/icons-material/ViewList';
import AccountBalanceWalletIcon from '@mui/icons-material/AccountBalanceWallet';
import { Fade } from '@mui/material';
import './Nominees.scss';
import { CategoryContent } from '../Categories/Category.types';
import { ProposalContent } from './Nominees.type';
import SUMMIT2023CONTENT from '../../common/resources/data/summit2023Content.json';
import { eventBus } from '../../utils/EventBus';
import { useCardano } from '@cardano-foundation/cardano-connect-with-wallet';
import { ROUTES } from '../../routes';
import { useSelector } from 'react-redux';
import { RootState } from '../../store';
import { ProposalPresentation } from '../../types/voting-ledger-follower-types';
import SidePage from 'components/common/SidePage/SidePage';
import { useToggle } from 'common/hooks/useToggle';
import ReadMore from './ReadMore';

const Nominees = () => {
  const { categoryId } = useParams();
  const navigate = useNavigate();
  const eventCache = useSelector((state: RootState) => state.user.event);
  const categories = eventCache?.categories;
  const categories_ids = categories?.map((e) => e.id);
  if (categoryId && !categories_ids?.includes(categoryId)) navigate(ROUTES.NOT_FOUND);
  const summit2023Category: CategoryContent = SUMMIT2023CONTENT.categories.find(
    (category) => category.id === categoryId
  );
  const summit2023CategoryNominees: ProposalContent[] = summit2023Category.proposals;
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('sm'));

  const [listView, setListView] = useState<'grid' | 'list'>('grid');
  const [isVisible, setIsVisible] = useState(true);
  const [isToggleReadMore, toggleReadMore] = useToggle(false);
  const [selectedNominee, setSelectedNominee] = useState({});
  const [nominees, setNominees] = useState<ProposalPresentation[]>([]);

  const { isConnected } = useCardano();

  useEffect(() => {
    if (isMobile) {
      setListView('list');
    }
  }, [isMobile]);

  const loadNominees = () => {
    if (categoryId) {
      categories?.map((category) => {
        if (category.id === categoryId) {
          setNominees(category?.proposals || []);
        }
      });
    } else {
      navigate(ROUTES.NOT_FOUND);
    }
  };

  useEffect(() => {
    loadNominees();
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

  const handleReadMore = (nominee) => {
    setSelectedNominee(nominee);
    toggleReadMore();
  };

  return (
    <>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 20 }}>
        <Typography
          className="nominees-title"
          variant="h4"
        >
          {summit2023Category.presentationName}
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
        {summit2023Category.desc}
      </Typography>

      <Grid
        container
        spacing={3}
        style={{ justifyContent: 'center' }}
      >
        {nominees.map((nominee, index) => (
          <Grid
            item
            xs={!isMobile && listView === 'grid' ? 4 : 12}
            key={nominee.id}
          >
            <Fade in={isVisible}>
              <Card
                className={'nominee-card'}
                style={{
                  padding: '8px',
                  width: listView === 'list' ? '100%' : '414px',
                  height: !isMobile && listView === 'list' ? 'auto' : isMobile ? '440px' : '390px',
                }}
              >
                <CardContent>
                  <Typography
                    className="nominee-title"
                    variant="h2"
                  >
                    {nominee.id === summit2023CategoryNominees[index].id
                      ? summit2023CategoryNominees[index].presentationName
                      : ''}
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
                        {nominee.id === summit2023CategoryNominees[index].id
                          ? summit2023CategoryNominees[index].desc
                          : ''}
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
                    onClick={() =>
                      handleReadMore(
                        nominee.id === summit2023CategoryNominees[index].id && summit2023CategoryNominees[index]
                      )
                    }
                    sx={{ cursor: 'pointer' }}
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

      <SidePage
        anchor="right"
        open={isToggleReadMore}
        setOpen={toggleReadMore}
      >
        <ReadMore
          nominee={selectedNominee}
          closeSidePage={toggleReadMore}
        />
      </SidePage>
    </>
  );
};

export { Nominees };
