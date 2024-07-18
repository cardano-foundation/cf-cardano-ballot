import React, { useState } from "react";
import {
  Box,
  Typography,
  Grid,
  Container,
  TableContainer,
  Table,
  TableHead,
  TableRow,
  TableCell,
  TableBody,
} from "@mui/material";
import theme from "../../common/styles/theme";
import MoreVertIcon from "@mui/icons-material/MoreVert";
import { PieChart, pieChartDefaultProps } from "react-minimal-pie-chart";
import leaderboard1Bg from "../../assets/bg/leaderboard1.svg";
import { addressSlice } from "../../utils/utils";
import { categoriesData } from "../../__fixtures__/leaderboard";
import { PageBase } from "../BasePage";

const Leaderboard: React.FC = () => {
  const [selected, setSelected] = useState<number | undefined>(undefined);
  const [hovered, setHovered] = useState<number | undefined>(undefined);

  const colors = [
    "#F8D6C3",
    "#F3B795",
    "#EE9868",
    "#E9793A",
    "#D85D18",
    "#AA4913",
    "#7C350E",
    "#4E2209",
    "#200E04",
  ];

  const dataForChart = categoriesData.map((item, index) => ({
    title: item.category,
    value: item.votes,
    color: colors[index % colors.length],
  }));

  return (
    <>
      <PageBase title="Categories">
        <>
          <Box
            component="div"
            sx={{
              height: "28px",
            }}
          />
          <Container>
            <Box component="div" sx={{ my: 4 }}>
              <Typography
                sx={{
                  color: theme.palette.text.neutralLightest,
                  fontFamily: "Dosis",
                  fontSize: "32px",
                  fontStyle: "normal",
                  fontWeight: 700,
                  lineHeight: "36px",
                  marginBottom: "32px",
                }}
              >
                Leaderboard
              </Typography>
              <Grid container spacing={2}>
                <Grid item xs={12} md={6}>
                  <Box
                    component="div"
                    sx={{
                      p: "28px",
                      backgroundImage: `url(${leaderboard1Bg})`,
                      backgroundSize: "200% 200%",
                      backgroundPosition: "center",
                      borderRadius: "24px",
                      backdropFilter: "blur(5px)",
                    }}
                  >
                    <Box
                      component="div"
                      sx={{
                        display: "flex",
                        justifyContent: "space-between",
                        alignItems: "center",
                      }}
                    >
                      <Typography
                        sx={{
                          fontFamily: "Dosis",
                          color: theme.palette.text.neutralLightest,
                          textShadow: "0px 0px 12px rgba(18, 18, 18, 0.20)",
                          fontSize: "28px",
                          fontStyle: "normal",
                          fontWeight: 700,
                          lineHeight: "32px",
                        }}
                      >
                        Total Votes
                      </Typography>
                      <MoreVertIcon
                        sx={{
                          cursor: "pointer",
                        }}
                      />
                    </Box>
                    <Typography
                      sx={{
                        my: 2,
                        fontFamily: "Dosis",
                        color: theme.palette.text.neutralLightest,
                        textShadow: "0px 0px 12px rgba(18, 18, 18, 0.20)",
                        fontSize: "36px",
                        fontStyle: "normal",
                        fontWeight: 700,
                        lineHeight: "40px",
                      }}
                    >
                      1,000
                    </Typography>
                    <TableContainer>
                      <Table size="small">
                        <TableHead>
                          <TableRow>
                            <TableCell
                              sx={{
                                fontWeight: "bold",
                                width: "50%",
                                padding: "12px 0px",
                              }}
                            >
                              Category
                            </TableCell>
                            <TableCell
                              sx={{
                                fontWeight: "bold",
                                width: "25%",
                                padding: "12px 0px",
                              }}
                              align="left"
                            >
                              Votes
                            </TableCell>
                            <TableCell
                              sx={{
                                fontWeight: "bold",
                                width: "25%",
                                padding: "12px 0px",
                              }}
                              align="left"
                            >
                              Percentage
                            </TableCell>
                          </TableRow>
                        </TableHead>
                        <TableBody>
                          {Array.from({ length: 10 }).map((_, index) => (
                            <TableRow key={index}>
                              <TableCell
                                component="th"
                                scope="row"
                                sx={{
                                  color: theme.palette.text.neutralLightest,
                                  textShadow:
                                    "0px 0px 12px rgba(18, 18, 18, 0.20)",
                                  fontSize: "12px",
                                  fontStyle: "normal",
                                  fontWeight: 700,
                                  lineHeight: "20px",
                                  padding: "12px 0px",
                                }}
                              >
                                Category {index + 1}
                              </TableCell>
                              <TableCell align="left">{100 + index}</TableCell>
                              <TableCell align="left">{10 + index}%</TableCell>
                            </TableRow>
                          ))}
                        </TableBody>
                      </Table>
                    </TableContainer>
                  </Box>
                </Grid>
                <Grid item xs={12} md={6}>
                  <Box
                    component="div"
                    sx={{
                      height: "100%",
                      paddingTop: "28px",
                      display: "flex",
                      flexDirection: "column",
                      alignItems: "center",
                      p: "28px",
                      backgroundImage: `url(${leaderboard1Bg})`,
                      backgroundSize: "200% 200%",
                      backgroundPosition: "center",
                      borderRadius: "24px",
                      backdropFilter: "blur(5px)",
                    }}
                  >
                    <Box
                      component="div"
                      sx={{
                        display: "flex",
                        justifyContent: "space-between",
                        width: "100%",
                      }}
                    >
                      <Typography
                        sx={{
                          color: theme.palette.text.neutralLightest,
                          textShadow: "0px 0px 12px rgba(18, 18, 18, 0.20)",
                          fontFamily: "Dosis",
                          fontSize: "28px",
                          fontStyle: "normal",
                          fontWeight: 700,
                          lineHeight: "32px",
                        }}
                      >
                        Votes per category
                      </Typography>
                      <MoreVertIcon
                        sx={{
                          cursor: "pointer",
                        }}
                      />
                    </Box>
                    <Box
                      component="div"
                      sx={{
                        marginTop: "48px",
                      }}
                    >
                      <Box
                        component="div"
                        sx={{
                          position: "relative",
                          maxWidth: 380,
                          height: 380,
                          margin: "auto",
                        }}
                      >
                        <PieChart
                          data={dataForChart.map((entry, index) => ({
                            ...entry,
                            color:
                              hovered === index ? entry.color : entry.color,
                          }))}
                          style={{ height: "100%" }}
                          lineWidth={30}
                          radius={pieChartDefaultProps.radius - 6}
                          segmentsStyle={{
                            transition: "stroke .3s",
                            cursor: "pointer",
                          }}
                          segmentsShift={(index) =>
                            index === selected ? 6 : 1
                          }
                          onClick={(_, index) => {
                            setSelected(index === selected ? undefined : index);
                          }}
                          onMouseOver={(_, index) => {
                            setHovered(index);
                            setSelected(index);
                          }}
                          onMouseOut={() => {
                            setHovered(undefined);
                          }}
                        />
                        <Box
                          component="div"
                          sx={{
                            position: "absolute",
                            top: "50%",
                            left: "50%",
                            transform: "translate(-50%, -50%)",
                            textAlign: "center",
                          }}
                        >
                          <Typography
                            sx={{
                              color: theme.palette.text.neutralLightest,
                              fontSize: "16px",
                              fontStyle: "normal",
                              fontWeight: 500,
                              lineHeight: "24px",
                            }}
                          >
                            Votes
                          </Typography>
                          <Typography
                            variant="h6"
                            component="div"
                            sx={{
                              color: theme.palette.text.neutralLightest,
                              fontFamily: "Dosis",
                              fontSize: "28px",
                              fontStyle: "normal",
                              fontWeight: 700,
                              lineHeight: "32px",
                            }}
                          >
                            {selected !== undefined
                              ? `${dataForChart[selected].value} Votes`
                              : "1,000"}
                          </Typography>
                        </Box>
                      </Box>

                      <Box
                        component="div"
                        sx={{
                          display: "flex",
                          mt: 2,
                          maxWidth: "100%",
                          flexWrap: "wrap",
                        }}
                      >
                        {dataForChart.map((entry, index) => (
                          <Box
                            component="div"
                            key={index}
                            sx={{
                              display: "flex",
                              alignItems: "center",
                              px: "12px",
                            }}
                          >
                            <Box
                              component="div"
                              sx={{
                                width: "12px",
                                height: "12px",
                                borderRadius: "4px",
                                backgroundColor: entry.color,
                                mr: 1,
                              }}
                            />
                            <Typography
                              sx={{
                                overflow: "hidden",
                                color: theme.palette.text.neutralLightest,
                                textOverflow: "ellipsis",
                                fontStyle: "normal",
                                fontWeight: 400,
                                lineHeight: "16px",
                                fontSize: "12px",
                                marginTop: "8px",
                              }}
                            >
                              {addressSlice(entry.title, 12, "end")}
                            </Typography>
                          </Box>
                        ))}
                      </Box>
                    </Box>
                  </Box>
                </Grid>
              </Grid>
            </Box>
          </Container>
        </>
      </PageBase>
    </>
  );
};

export { Leaderboard };
