import React, { useState } from 'react';
import { Avatar, Button, CardActionArea, CardActions, CardHeader } from '@mui/material';
import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import CardMedia from '@mui/material/CardMedia';
import { Container, Box, Grid, Typography } from '@mui/material';
import CATEGORY_IMAGES from '../../common/resources/data/categoryImages.json';
import CATEGORIES from '../../common/resources/data/categoriesData.json';
import { Link } from 'react-router-dom';
import NavigateNextIcon from '@mui/icons-material/NavigateNext';
import Grow from '@mui/material/Grow';

const Categories = () => {
  const categories = CATEGORIES.data;
  const [isHoveredId, setIsHoveredId] = useState('');

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
      <Container maxWidth="lg">
        <Grid
          container
          direction="column"
          justifyContent="center"
          alignItems="center"
        >
          <Grid
            item
            xs={12}
            sx={{ mb: 5 }}
          >
            <Grid
              container
              spacing={3}
              direction="row"
              justifyContent="center"
              alignItems="center"
            >
              {categories.map((category, index) => {
                return (
                  <Grid
                    key={category.id}
                    item
                    xs="auto"
                  >
                    <Card
                      sx={{ width: 330, borderRadius: 8 }}
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
                                variant="h6"
                                sx={{ color: 'white', fontWeight: 600 }}
                              >
                                {category.presentationName}
                              </Typography>
                            </Box>
                            <Box
                              sx={{
                                position: 'absolute',
                                bottom: '20%',
                                right: 0,
                                width: '100%',
                                paddingLeft: '20px',
                                textAlign: 'right',
                              }}
                            >
                              <NavigateNextIcon
                                sx={{
                                  fontSize: 25,
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
                  </Grid>
                );
              })}
            </Grid>
          </Grid>
        </Grid>
      </Container>
    </>
  );
};

export { Categories };
