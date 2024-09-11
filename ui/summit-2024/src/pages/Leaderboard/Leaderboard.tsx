import React, { useEffect, useState } from "react";
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
  Fade,
} from "@mui/material";
import theme from "../../common/styles/theme";
import MoreVertIcon from "@mui/icons-material/MoreVert";
import { PieChart, pieChartDefaultProps } from "react-minimal-pie-chart";
import leaderboard1Bg from "../../assets/leaderboard1.svg";

import {
  addressSlice,
  calculateTotalVotes,
  formatISODate,
} from "../../utils/utils";
import { PageBase } from "../BasePage";
import AnimatedSwitch from "../../components/AnimatedSwitch/AnimatedSwitch";
import { Categories } from "../Categories";
import { getStats } from "../../common/api/leaderboardService";
import { ByCategoryStats } from "../../types/voting-app-types";
import { useAppSelector } from "../../store/hooks";
import { getEventCache } from "../../store/reducers/eventCache";

const Leaderboard: React.FC = () => {
  const [stats, setStats] = useState<ByCategoryStats[]>();
  const eventCache = useAppSelector(getEventCache);

  const [selected, setSelected] = useState<number | undefined>(undefined);
  const [hovered, setHovered] = useState<number | undefined>(undefined);
  const [content, setContent] = useState("Overall Votes");
  const [fade, setFade] = useState(true);

  const showRevealDate = eventCache.finished && !eventCache.proposalsReveal;
  const showWinners = eventCache.proposalsReveal;

  const nameMap = new Map(
    eventCache.categories.map((category) => [category.id, category.name]),
  );

  const extendedStats = stats?.map((item) => ({
    ...item,
    name: nameMap.get(item.id) || null,
  }));

  useEffect(() => {
    getStats().then((response) => {
      // @ts-ignore
      setStats(response.categories);
    });
  }, []);

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

  const dataForChart = extendedStats?.map((item, index) => ({
    title: item.name,
    value: item.votes,
    color: colors[index % colors.length],
  }));

  const totalVotes = calculateTotalVotes(stats);

  let selectedCategoryValue = -1;
  if (dataForChart !== undefined && selected !== undefined) {
    selectedCategoryValue = dataForChart[selected].value;
  }

  const handleSwitch = (option: string) => {
    if (option !== content) {
      setFade(false);
      setTimeout(() => {
        setContent(option);
        setFade(true);
      }, 500);
    }
  };

  return (
    <>
      <PageBase title="Categories">
        <>
          <Container>
            <Box
              component="div"
              sx={{
                display: "flex",
                flexDirection: { xs: "column", md: "row" },
                alignItems: { xs: "flex-start", md: "center" },
                justifyContent: { md: "space-between" },
                width: "100%",
                paddingTop: {
                  xs: content === "Winners" ? "60px" : "20px",
                  md: "40px",
                },
                paddingBottom: { xs: "26px" },
              }}
            >
              <Typography
                sx={{
                  color: theme.palette.text.neutralLightest,
                  fontFamily: "Dosis",
                  fontSize: "32px",
                  fontStyle: "normal",
                  fontWeight: 700,
                  lineHeight: "36px",
                  textAlign: "left",
                  marginBottom: { xs: 2, md: 0 },
                }}
              >
                Leaderboard
              </Typography>

              {showRevealDate ? (
                <Typography
                  sx={{
                    color: theme.palette.text.neutralLightest,
                    fontFamily: "Dosis",
                    fontSize: "32px",
                    fontStyle: "normal",
                    fontWeight: 700,
                    lineHeight: "36px",
                    textAlign: "left",
                    marginBottom: { xs: 2, md: 0 },
                  }}
                >
                  {"Voting Results " +
                    formatISODate(eventCache.proposalsRevealDate)}
                </Typography>
              ) : undefined}
              {showWinners ? (
                <AnimatedSwitch
                  defaultValue="Overall Votes"
                  optionA="Winners"
                  optionB="Overall Votes"
                  onClickOption={handleSwitch}
                />
              ) : undefined}
            </Box>
            <Fade
              in={fade}
              timeout={400}
              onExited={() =>
                handleSwitch(
                  content === "Winners" ? "Overall Votes" : "Winners",
                )
              }
            >
              <Box
                component="div"
                sx={{
                  width: "100%",
                }}
              >
                {content === "Winners" ? (
                  <>
                    <Categories />
                  </>
                ) : (
                  <>
                    <Box component="div">
                      <Grid container spacing={2}>
                        <Grid item xs={12} md={6}>
                          <Box
                            component="div"
                            sx={{
                              p: "28px",
                              backgroundImage: `url(${leaderboard1Bg})`,
                              backgroundSize: "cover",
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
                                  textShadow:
                                    "0px 0px 12px rgba(18, 18, 18, 0.20)",
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
                                textShadow:
                                  "0px 0px 12px rgba(18, 18, 18, 0.20)",
                                fontSize: "36px",
                                fontStyle: "normal",
                                fontWeight: 700,
                                lineHeight: "40px",
                              }}
                            >
                              {totalVotes}
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
                                  {extendedStats?.map((item, index) => (
                                    <TableRow key={index}>
                                      <TableCell
                                        component="th"
                                        scope="row"
                                        sx={{
                                          color:
                                            theme.palette.text.neutralLightest,
                                          textShadow:
                                            "0px 0px 12px rgba(18, 18, 18, 0.20)",
                                          fontSize: "12px",
                                          fontStyle: "normal",
                                          fontWeight: 700,
                                          lineHeight: "20px",
                                          padding: "12px 0px",
                                        }}
                                      >
                                        {item.name}
                                      </TableCell>
                                      <TableCell align="left">
                                        {item.votes}
                                      </TableCell>
                                      <TableCell align="left">
                                        {(
                                          (item.votes / totalVotes) *
                                          100
                                        ).toFixed(2)}
                                        %
                                      </TableCell>
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
                              backgroundSize: "cover",
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
                                  textShadow:
                                    "0px 0px 12px rgba(18, 18, 18, 0.20)",
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
                                  // @ts-ignore
                                  data={dataForChart?.map((entry, index) => ({
                                    ...entry,
                                    color:
                                      hovered === index
                                        ? entry.color
                                        : entry.color,
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
                                    setSelected(
                                      index === selected ? undefined : index,
                                    );
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
                                    {selectedCategoryValue > 0
                                      ? `${selectedCategoryValue} Votes`
                                      : totalVotes}
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
                                {dataForChart?.map((entry, index) => (
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
                                        color:
                                          theme.palette.text.neutralLightest,
                                        textOverflow: "ellipsis",
                                        fontStyle: "normal",
                                        fontWeight: 400,
                                        lineHeight: "16px",
                                        fontSize: "12px",
                                        marginTop: "8px",
                                      }}
                                    >
                                      {addressSlice(
                                        entry?.title || "",
                                        12,
                                        "end",
                                      )}
                                    </Typography>
                                  </Box>
                                ))}
                              </Box>
                            </Box>
                          </Box>
                        </Grid>
                      </Grid>
                    </Box>
                  </>
                )}
              </Box>
            </Fade>
          </Container>
        </>
      </PageBase>
    </>
  );
};

export { Leaderboard };
