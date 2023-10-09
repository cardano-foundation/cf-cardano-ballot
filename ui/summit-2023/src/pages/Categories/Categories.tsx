import React, { useState, useEffect, ReactElement } from 'react';
import {
  useTheme,
  useMediaQuery,
  Typography,
  IconButton,
  Grid,
  Card,
  CardContent,
  Button,
  CardActionArea,
  CardHeader,
  Avatar,
  Box,
  CardActions,
  Tooltip,
  Hidden,
} from '@mui/material';
import checkMark from '../../common/resources/images/checkmark-white.png';
import labelVoted from '../../common/resources/images/checkmark-green.png';
import ViewModuleIcon from '@mui/icons-material/ViewModule';
import ViewListIcon from '@mui/icons-material/ViewList';
import { Fade } from '@mui/material';
import styles from './Categories.module.scss';
import Grow from '@mui/material/Grow';
import CATEGORY_IMAGES from '../../common/resources/data/categoryImages.json';
import { Link } from 'react-router-dom';
import CardMedia from '@mui/material/CardMedia';
import NavigateNextIcon from '@mui/icons-material/NavigateNext';
import { useSelector } from 'react-redux';
import { RootState } from '../../store';
import { getUserInSession, tokenIsExpired } from '../../utils/session';

const categoryAlreadyVoted = (categoryId, userVotes) => {
  let alreadyVoted = false;
  const session = getUserInSession();
  if (!tokenIsExpired(session?.expiresAt) && userVotes?.length && userVotes?.find((c) => c.categoryId === categoryId)) {
    alreadyVoted = true;
  }
  return alreadyVoted;
};

const Categories = () => {
  const eventCache = useSelector((state: RootState) => state.user.event);
  const userVotes = useSelector((state: RootState) => state.user.userVotes);
  const categories = eventCache?.categories;

  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('sm'));
  const isBigScreen = useMediaQuery(theme.breakpoints.down('xl'));

  const [listView, setListView] = useState<'grid' | 'list'>('grid');
  const [isVisible, setIsVisible] = useState(true);
  const [isHoveredId, setIsHoveredId] = useState('');

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

  const handleMouseOver = (id: any) => {
    return () => {
      setIsHoveredId(id);
    };
  };

  const handleMouseOut = () => {
    setIsHoveredId('');
  };

  const renderResponsiveGrid = (items): ReactElement => {
    return (
      <div>
        <Grid
          container
          spacing={3}
          justifyContent="center"
        >
          {items.map((category, index) => {
            const voted = categoryAlreadyVoted(category.id, userVotes);
            return (
              <Grid
                item
                xs={12}
                sm={6}
                md={4}
                key={category.id}
              >
                <Fade in={isVisible}>
                  <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                    <Card
                      sx={{
                        height: 'auto',
                        width: { xs: '90vw', sm: '50vw' },
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                        borderRadius: '16px',
                      }}
                    >
                      {!isMobile ? (
                        <CardActionArea
                          onMouseOver={handleMouseOver(category.id)}
                          onMouseOut={handleMouseOut}
                        >
                          {isHoveredId == category.id ? (
                            <Grow
                              in
                              style={{ transformOrigin: '10 0 0' }}
                              {...{ timeout: 600 }}
                            >
                              <CardContent sx={{ minHeight: '350px', maxHeight: '350px' }}>
                                <Box sx={{ position: 'relative' }}>
                                  {voted ? (
                                    <Tooltip title="Already Voted">
                                      <img
                                        height={40}
                                        width={102}
                                        src={labelVoted}
                                        alt="Already Voted"
                                        style={{
                                          margin: '12px',
                                          position: 'absolute',
                                          float: 'right',
                                          right: 0,
                                          zIndex: 99,
                                          opacity: 1,
                                        }}
                                      />
                                    </Tooltip>
                                  ) : null}
                                </Box>
                                <CardHeader
                                  avatar={
                                    <Avatar
                                      src={CATEGORY_IMAGES[index]}
                                      alt={category.presentationName}
                                      sx={{ width: 100, height: 100 }}
                                    />
                                  }
                                />
                                <Box m={1}>
                                  <Typography
                                    variant="h5"
                                    color="text.primary"
                                    fontWeight="700"
                                  >
                                    {category.presentationName}
                                  </Typography>
                                </Box>
                                <Box m={1}>
                                  <Typography
                                    variant="body1"
                                    color="text.primary"
                                  >
                                    {category.desc}
                                  </Typography>
                                </Box>
                                <CardActions>
                                  <Button
                                    component={Link}
                                    to={{ pathname: `/nominees/${category.id}` }}
                                    state={{
                                      category,
                                    }}
                                    aria-label="View Nominees"
                                    variant="contained"
                                    size="large"
                                    sx={{
                                      color: 'text.primary',
                                      fontSize: 16,
                                      fontWeight: 700,
                                      textTransform: 'none',
                                      width: '100%',
                                      backgroundColor: '#acfcc5 !important',
                                    }}
                                  >
                                    View Nominees
                                  </Button>
                                </CardActions>
                              </CardContent>
                            </Grow>
                          ) : (
                            <Box>
                              <Box sx={{ position: 'relative' }}>
                                {voted ? (
                                  <Tooltip title="Already Voted">
                                    <img
                                      height={40}
                                      width={40}
                                      src={checkMark}
                                      alt="Already Voted"
                                      style={{
                                        margin: '12px',
                                        position: 'absolute',
                                        float: 'right',
                                        right: 0,
                                        zIndex: 99,
                                        opacity: 1,
                                      }}
                                    />
                                  </Tooltip>
                                ) : null}
                                <CardMedia
                                  sx={{
                                    height: 350,
                                    cursor: 'pointer',
                                    '&:hover': {
                                      borderRadius: '50%',
                                      transition: 'all 1s ease',
                                    },
                                  }}
                                  image={CATEGORY_IMAGES[index]}
                                />
                              </Box>
                              <Box
                                sx={{
                                  position: 'absolute',
                                  bottom: '20%',
                                  left: 0,
                                  width: '100%',
                                  paddingLeft: '20px',
                                  textAlign: 'left',
                                }}
                              >
                                <Typography
                                  variant="h6"
                                  sx={{
                                    color: 'white',
                                    fontSize: {
                                      xs: '28px',
                                      sm: '28px',
                                      md: '32px',
                                    },
                                    fontWeight: 600,
                                    wordBreak: 'break-word',
                                    maxWidth: '250px',
                                  }}
                                >
                                  {category.presentationName}
                                </Typography>
                              </Box>
                              <Box
                                sx={{
                                  position: 'absolute',
                                  bottom: isMobile ? '8%' : '20%',
                                  right: 0,
                                  width: '100%',
                                  paddingLeft: '20px',
                                  textAlign: 'right',
                                }}
                              >
                                <NavigateNextIcon
                                  sx={{
                                    fontSize: '50px',
                                    margin: '0px 20px -7px 20px',
                                    borderRadius: 25,
                                    backgroundColor: '#acfcc5 !important',
                                  }}
                                />
                              </Box>
                            </Box>
                          )}
                        </CardActionArea>
                      ) : (
                        <CardContent sx={{ minHeight: '350px', maxHeight: '350px' }}>
                          <Box sx={{ position: 'relative' }}>
                            {voted ? (
                              <Tooltip title="Already Voted">
                                <img
                                  height={40}
                                  width={102}
                                  src={labelVoted}
                                  alt="Already Voted"
                                  style={{
                                    margin: '12px',
                                    position: 'absolute',
                                    float: 'right',
                                    right: 0,
                                    zIndex: 99,
                                    opacity: 1,
                                  }}
                                />
                              </Tooltip>
                            ) : null}
                          </Box>
                          <CardHeader
                            avatar={
                              <Avatar
                                src={CATEGORY_IMAGES[index]}
                                alt={category.presentationName}
                                sx={{ width: 100, height: 100 }}
                              />
                            }
                          />
                          <Box m={1}>
                            <Typography
                              variant="h5"
                              color="text.primary"
                              fontWeight="700"
                            >
                              {category.presentationName}
                            </Typography>
                          </Box>
                          <Box m={1}>
                            <Typography
                              variant="body1"
                              color="text.primary"
                            >
                              {category.desc}
                            </Typography>
                          </Box>
                          <CardActions>
                            <Button
                              component={Link}
                              to={{ pathname: `/nominees/${category.id}` }}
                              state={{
                                category,
                              }}
                              aria-label="View Nominees"
                              variant="contained"
                              size="large"
                              sx={{
                                color: 'text.primary',
                                fontSize: 16,
                                fontWeight: 700,
                                textTransform: 'none',
                                width: '100%',
                                backgroundColor: '#acfcc5 !important',
                              }}
                            >
                              View Nominees
                            </Button>
                          </CardActions>
                        </CardContent>
                      )}
                    </Card>
                  </div>
                </Fade>
              </Grid>
            );
          })}
        </Grid>
      </div>
    );
  };
  const renderResponsiveList = (items): ReactElement => {
    return (
      <div style={{ width: '100%' }}>
        <Grid
          container
          spacing={3}
          justifyContent="center"
        >
          {items.map((category, index) => {
            const voted = categoryAlreadyVoted(category.id, userVotes);
            return (
              <Grid
                item
                xs={12}
                key={category.id}
              >
                <Fade in={isVisible}>
                  <Card
                    className="categories-card"
                    sx={{
                      width: listView === 'list' || isMobile ? '100%' : '410px',
                      height: 'auto',
                    }}
                    key={category.id}
                  >
                    <Box sx={{ position: 'relative' }}>
                      {voted ? (
                        <Tooltip title="Already Voted">
                          <img
                            height={40}
                            width={102}
                            src={labelVoted}
                            alt="Already Voted"
                            style={{
                              margin: '12px',
                              position: 'absolute',
                              float: 'right',
                              right: 0,
                              zIndex: 99,
                              opacity: 1,
                            }}
                          />
                        </Tooltip>
                      ) : null}
                    </Box>
                    <CardContent sx={{ display: 'flex', alignItems: 'center' }}>
                      <CardHeader
                        avatar={
                          <Avatar
                            src={CATEGORY_IMAGES[index]}
                            alt={category.presentationName}
                            sx={{ width: 100, height: 100 }}
                          />
                        }
                      />
                      <Box sx={{ mx: 1, display: 'flex', flexDirection: 'column' }}>
                        <Typography
                          variant="h5"
                          color="text.primary"
                          fontWeight="700"
                        >
                          {category.presentationName}
                        </Typography>
                        <Typography
                          variant="body1"
                          color="text.primary"
                        >
                          {category.desc}
                        </Typography>
                      </Box>
                      <Box sx={{ marginLeft: 'auto', display: { sm: 'none', md: 'block' } }}>
                        <Button
                          component={Link}
                          to={{ pathname: `/nominees/${category.id}` }}
                          state={{
                            category,
                          }}
                          aria-label="View Nominees"
                          variant="contained"
                          size="large"
                          sx={{
                            width: '100%',
                            color: 'text.primary',
                            fontSize: 16,
                            fontWeight: 700,
                            textTransform: 'none',
                            borderRadius: '8px',
                            backgroundColor: '#acfcc5 !important',
                            marginRight: '28px',
                            minWidth: '166px',
                          }}
                        >
                          View Nominees
                        </Button>
                      </Box>
                    </CardContent>
                    <Box sx={{ display: { sm: 'block', md: 'none' } }}>
                      <Button
                        component={Link}
                        to={{ pathname: `/nominees/${category.id}` }}
                        state={{
                          category,
                        }}
                        aria-label="View Nominees"
                        variant="contained"
                        size="large"
                        sx={{
                          width: '95%',
                          color: 'text.primary',
                          fontSize: 16,
                          fontWeight: 700,
                          textTransform: 'none',
                          borderRadius: '8px',
                          backgroundColor: '#acfcc5 !important',
                          margin: '20px',
                          minWidth: '166px',
                        }}
                      >
                        View Nominees
                      </Button>
                    </Box>
                  </Card>
                </Fade>
              </Grid>
            );
          })}
        </Grid>
      </div>
    );
  };

  return (
    <div
      data-testid="categories-page"
      className={styles.categories}
      style={{ padding: isBigScreen ? '0px' : '0px 10px' }}
    >
      <Grid
        container
        direction="row"
        justifyContent="space-between"
        alignItems="center"
      >
        <Grid item>
          <Typography
            variant="h2"
            fontSize={{
              xs: '28px',
              md: '32px',
              lg: '48px',
            }}
            lineHeight={{
              xs: '28px',
              md: '32px',
            }}
            sx={{
              color: '#24262E',
              fontStyle: 'normal',
              fontWeight: '600',
            }}
          >
            Categories
          </Typography>
        </Grid>
        <Grid item>
          <Hidden smDown>
            <IconButton
              onClick={() => handleListView('grid')}
              className={listView === 'grid' ? styles.selected : styles.unSelected}
            >
              <ViewModuleIcon />
            </IconButton>
            <IconButton
              onClick={() => handleListView('list')}
              className={listView === 'list' ? styles.selected : styles.unSelected}
            >
              <ViewListIcon />
            </IconButton>
          </Hidden>
        </Grid>
      </Grid>

      <Box my={6}>
        {isMobile || listView === 'grid' ? renderResponsiveGrid(categories) : renderResponsiveList(categories)}
      </Box>
    </div>
  );
};

export { Categories, categoryAlreadyVoted };
