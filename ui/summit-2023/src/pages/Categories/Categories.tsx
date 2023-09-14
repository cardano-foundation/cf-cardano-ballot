import React, { useState, useEffect } from 'react';
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
} from '@mui/material';
import ViewModuleIcon from '@mui/icons-material/ViewModule';
import ViewListIcon from '@mui/icons-material/ViewList';
import { Fade } from '@mui/material';
import styles from './Categories.module.scss';
import { CategoryContent } from './Category.types';
import Grow from '@mui/material/Grow';
import CATEGORY_IMAGES from '../../common/resources/data/categoryImages.json';
import { Link } from 'react-router-dom';
import CardMedia from '@mui/material/CardMedia';
import NavigateNextIcon from '@mui/icons-material/NavigateNext';
import SUMMIT2023CONTENT from '../../common/resources/data/summit2023Content.json';
import { useSelector } from 'react-redux';
import { RootState } from '../../store';

const Categories = () => {
  const eventCache = useSelector((state: RootState) => state.user.event);

  const categories = eventCache?.categories;
  const summit2023Categories: CategoryContent[] = SUMMIT2023CONTENT.categories;

  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('xs'));

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

  return (
    <div
      data-testid="categories-page"
      className={styles.categories}
    >
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 20 }}>
        <Typography
          className={styles.title}
          variant="h2"
          fontSize={{
            xs: '28px',
            md: '32px',
          }}
          lineHeight={{
            xs: '28px',
            md: '32px',
          }}
        >
          Categories
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

      <Grid
        container
        spacing={3}
        style={{ justifyContent: 'center' }}
      >
        {categories?.map((category, index) => (
          <Grid
            item
            xs={12}
            sm={6}
            md={!isMobile && listView === 'grid' ? 4 : 12}
            key={category.id}
          >
            {!isMobile && listView === 'grid' ? (
              <Fade in={isVisible}>
                <Card
                  className={styles.card}
                  sx={{
                    width: isMobile ? '100%' : '414px',
                  }}
                  key={category.id}
                >
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
                        <CardContent sx={{ minHeight: '350px' }}>
                          <CardHeader
                            avatar={
                              <Avatar
                                src={CATEGORY_IMAGES[index]}
                                alt={
                                  category.id === summit2023Categories[index].id
                                    ? summit2023Categories[index].presentationName
                                    : ''
                                }
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
                              {category.id === summit2023Categories[index].id
                                ? summit2023Categories[index].presentationName
                                : ''}
                            </Typography>
                          </Box>
                          <Box m={1}>
                            <Typography
                              variant="body1"
                              color="text.primary"
                            >
                              {category.id === summit2023Categories[index].id ? summit2023Categories[index].desc : ''}
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
                      <Box sx={{ position: 'relative' }}>
                        <Box>
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
                              fontSize: '36px',
                              fontWeight: 600,
                              wordBreak: 'break-word',
                              maxWidth: '285px',
                            }}
                          >
                            {category.id === summit2023Categories[index].id
                              ? summit2023Categories[index].presentationName
                              : ''}
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
                </Card>
              </Fade>
            ) : (
              <Fade in={isVisible}>
                <Card
                  className="categories-card"
                  sx={{
                    width: listView === 'list' || isMobile ? '100%' : '414px',
                    height: '156px',
                  }}
                  key={category.id}
                >
                  <CardContent sx={{ display: 'flex', alignItems: 'center' }}>
                    <CardHeader
                      avatar={
                        <Avatar
                          src={CATEGORY_IMAGES[index]}
                          alt={
                            category.id === summit2023Categories[index].id
                              ? summit2023Categories[index].presentationName
                              : ''
                          }
                          sx={{ width: 100, height: 100 }}
                        />
                      }
                    />
                    <Box sx={{ marginLeft: 2, display: 'flex', flexDirection: 'column' }}>
                      <Typography
                        variant="h5"
                        color="text.primary"
                        fontWeight="700"
                      >
                        {category.id === summit2023Categories[index].id
                          ? summit2023Categories[index].presentationName
                          : ''}
                      </Typography>
                      <Typography
                        variant="body1"
                        color="text.primary"
                      >
                        {category.id === summit2023Categories[index].id ? summit2023Categories[index].desc : ''}
                      </Typography>
                    </Box>
                    <Box sx={{ marginLeft: 'auto' }}>
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
                          backgroundColor: '#acfcc5 !important',
                          marginRight: '28px',
                        }}
                      >
                        View Nominees
                      </Button>
                    </Box>
                  </CardContent>
                </Card>
              </Fade>
            )}
          </Grid>
        ))}
      </Grid>
    </div>
  );
};

export { Categories };
