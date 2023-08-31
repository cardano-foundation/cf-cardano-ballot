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
import './Categories.scss';
import Grow from '@mui/material/Grow';
import CATEGORY_IMAGES from '../../common/resources/data/categoryImages.json';
import { Link } from 'react-router-dom';
import CardMedia from '@mui/material/CardMedia';
import NavigateNextIcon from '@mui/icons-material/NavigateNext';
import CATEGORIES from '../../common/resources/data/categoriesData.json';

const Categories = () => {
  const categories = CATEGORIES.data;
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('sm'));

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
    <>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 20 }}>
        <Typography
          className="categories-title"
          variant="h4"
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
        {categories.map((category, index) => (
          <Grid
            item
            xs={!isMobile && listView === 'grid' ? 4 : 12}
            key={category.id}
          >
            {listView === 'grid' || isMobile ? (
              <Fade in={isVisible}>
                <Card
                  sx={{
                    width: isMobile ? '100%' : '414px',
                    borderRadius: 8
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
                              {category.description}
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
                            variant="h5"
                            sx={{
                              color: 'white',
                              fontWeight: 600,
                              wordBreak: 'break-word',
                              maxWidth: '285px',
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
                </Card>
              </Fade>
            ) : (
              <Fade in={isVisible}>
                <Card
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
                          alt={category.presentationName}
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
                        {category.presentationName}
                      </Typography>
                      <Typography
                        variant="body1"
                        color="text.primary"
                      >
                        {category.description}
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
    </>
  );
};

export { Categories };
