import React from 'react';
import { Box, Typography, Grid, Container } from '@mui/material';
import theme from "../../common/styles/theme";

const Leaderboard: React.FC = () => {
    return (
        <Container maxWidth="lg">
            <Box sx={{ my: 4 }}>
                <Typography sx={{
                    color: theme.palette.text.neutralLightest,
                    fontFamily: "Dosis",
                    fontSize: "32px",
                    fontStyle: "normal",
                    fontWeight: 700,
                    lineHeight: "36px",
                    marginBottom: "32px"
                }}>
                    Leaderboard
                </Typography>
                <Grid container spacing={2}>
                    <Grid item xs={12} md={6}>
                        <Box sx={{ p: 2, border: '1px solid #ccc', borderRadius: '4px' }}>
                            <Typography>
                                Left Column Content
                            </Typography>
                        </Box>
                    </Grid>
                    <Grid item xs={12} md={6}>
                        <Box sx={{ p: 2, border: '1px solid #ccc', borderRadius: '4px' }}>
                            <Typography>
                                Right Column Content
                            </Typography>
                        </Box>
                    </Grid>
                </Grid>
            </Box>
        </Container>
    );
};


export { Leaderboard };
