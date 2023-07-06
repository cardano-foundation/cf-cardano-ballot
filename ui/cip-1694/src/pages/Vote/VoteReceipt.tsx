import React, { useState } from 'react';
import Grid from '@mui/material/Grid';
import { Typography, Container } from '@mui/material';

const VoteReceipt = () => {

    return (
        <>
            <Container>
                <Grid
                    container
                    direction="column"
                    justifyContent="flex-start"
                    alignItems="flex-start"
                    spacing={2}
                >
                    <Grid item xs={12} sx={{ mt: 2, mb: 1 }}>
                        <Typography variant="h4">
                            Your Vote Receipt
                        </Typography>
                    </Grid>

                    <Grid item xs={6}>
                        <Typography variant="subtitle1">Event</Typography>
                    </Grid>
                    <Grid item xs={6}>
                        <Typography variant="body1">
                            
                        </Typography>
                    </Grid>
                    <Grid item xs={6}>
                        <Typography variant="subtitle1">Category</Typography>
                    </Grid>
                    <Grid item xs={6}>
                        <Typography variant="body1">

                        </Typography>
                    </Grid>
                    <Grid item xs={6}>
                        <Typography variant="subtitle1">
                            Your digital Receipt goes here...
                        </Typography>
                    </Grid>
                </Grid>
                
            </Container>
        </>
    );
};

export default VoteReceipt;