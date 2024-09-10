import React, { useEffect, useState } from "react";
import {
  Box,
  Fade,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Typography,
} from "@mui/material";
import awardImg from "../../../assets/awardWinner.svg";
import winnerBg from "../../../assets/bg/winnerBg.svg";
import votesIcon from "../../../assets/votesIcon.svg";
import positionIcon from "../../../assets/positionIcon.svg";
import nomineeIcon from "../../../assets/nomineeIcon.svg";
import tickIcon from "../../../assets/tickIcon.svg";
import theme from "../../../common/styles/theme";
import { useAppSelector } from "../../../store/hooks";
import { getEventCache } from "../../../store/reducers/eventCache";
import { getVotingResults } from "../../../common/api/leaderboardService";
import { Proposal } from "../../../store/reducers/eventCache/eventCache.types";
import { getVotes } from "../../../store/reducers/votesCache";

interface WinnersProps {
  fadeChecked: boolean;
  nominees: Proposal[];
  categoryId: string;
  handleOpenLearnMore: (nomineeId: string) => void;
}

const Winners: React.FC<WinnersProps> = ({
  fadeChecked,
  categoryId,
  handleOpenLearnMore,
}) => {
  const userVotes = useAppSelector(getVotes);
  const eventCache = useAppSelector(getEventCache);
  const [votingResults, setVotingResults] = useState([]);

  const votesMap = votingResults?.reduce(
    (acc, category) => {
      // @ts-ignore
      Object.keys(category.proposals).forEach((proposalId) => {
        // @ts-ignore
        acc[proposalId] = category.proposals[proposalId].votes;
      });
      return acc;
    },
    {} as { [key: string]: number },
  );

  const extendedCategoryData = eventCache.categories.map((category) => {
    return {
      ...category,
      proposals: category.proposals
        .map((proposal) => ({
          ...proposal,
          votes: votesMap[proposal.id] || 0,
        }))
        .sort((a, b) => b.votes! - a.votes!),
    };
  });

  const votedFor = userVotes.find(
    (v) => v.categoryId === categoryId,
  )?.proposalId;

  let nominees =
    extendedCategoryData.find((c) => c.id === categoryId)?.proposals || [];
  const winner = nominees[0];
  nominees.shift();

  useEffect(() => {
    getVotingResults().then((response) => {
      // @ts-ignore
      setVotingResults(response);
    });
  }, []);

  const handleLearnMoreClick = (
    event: React.MouseEvent<HTMLDivElement, MouseEvent>,
    nomineeId: string,
  ) => {
    event.stopPropagation();
    handleOpenLearnMore(nomineeId);
  };

  const TickIcon = ({
    circleSize,
    tickSize,
  }: {
    circleSize: number;
    tickSize: number;
  }) => {
    return (
      <Box
        component="div"
        sx={{
          width: circleSize ? circleSize : "24px",
          height: circleSize ? circleSize : "24px",
          backgroundColor: theme.palette.secondary.main,
          borderRadius: "50%",
          display: "flex",
          alignItems: "center",
          justifyContent: "center",
        }}
      >
        <img
          src={tickIcon}
          style={{
            width: tickSize ? tickSize : "16px",
            height: tickSize ? tickSize : "16px",
          }}
        />
      </Box>
    );
  };

  const Winner = () => (
    <Box
      component="div"
      sx={{
        width: "100%",
        borderRadius: "24px",
        overflow: "hidden",
        padding: "4px",
        background:
          "linear-gradient(45deg, #0C7BC5 0%, #40407D 50%, #EE9766 100%)",
        position: "sticky",
        top: 144,
        overflowY: "auto",
        maxHeight: "calc(100vh - 144px)",
      }}
    >
      <Box
        component="img"
        src={winnerBg}
        alt="Background"
        sx={{
          position: "absolute",
          top: 0,
          left: 0,
          width: "100%",
          height: "100%",
          borderRadius: "inherit",
          objectFit: "cover",
          zIndex: 1,
        }}
      />
      <Box
        component="div"
        sx={{
          position: "relative",
          borderRadius: "24px",
          color: "white",
          zIndex: 2,
          backgroundColor: "rgba(0, 0, 0, 0.5)",
          padding: "28px",
        }}
      >
        <Box
          component="span"
          sx={{
            position: "absolute",
            top: "14px",
            right: "14px",
            zIndex: 3,
          }}
        >
          {votedFor === winner.id ? (
            <TickIcon circleSize={28} tickSize={20} />
          ) : null}
        </Box>
        <Typography
          variant="h4"
          align="center"
          sx={{
            color: theme.palette.text.neutralLightest,
            textAlign: "center",
            textShadow: "0px 0px 12px rgba(18, 18, 18, 0.20)",
            fontFamily: "Dosis",
            fontSize: "36px",
            fontStyle: "normal",
            fontWeight: 700,
            lineHeight: "40px",
            marginBottom: "40px",
          }}
        >
          Winner!
        </Typography>
        <Box component="div" display="flex" justifyContent="center" mt={2}>
          <img src={awardImg} alt="Placeholder" height={148} />
        </Box>
        <Typography
          onClick={(event: React.MouseEvent<HTMLDivElement, MouseEvent>) =>
            handleLearnMoreClick(event, "")
          }
          align="center"
          mt={2}
          sx={{
            color: theme.palette.text.neutralLightest,
            textAlign: "center",
            textShadow: "0px 0px 12px rgba(18, 18, 18, 0.20)",
            fontFamily: "Dosis",
            fontSize: "28px",
            fontStyle: "normal",
            fontWeight: 700,
            lineHeight: "32px",
            marginTop: "40px",
            cursor: "pointer",
          }}
        >
          {winner?.name}
        </Typography>
        <Box
          component="div"
          sx={{
            display: "flex",
            justifyContent: "center",
            alignItems: "center",
            marginTop: "40px",
          }}
        >
          <Box
            component="div"
            sx={{
              display: "flex",
              alignItems: "center",
              flexDirection: "column",
              marginRight: { sm: "48px" },
            }}
          >
            <Box
              component="div"
              sx={{
                display: "flex",
                alignItems: "center",
              }}
            >
              <img
                src={votesIcon}
                alt="Total Votes Icon"
                width="24"
                height="24"
              />
              <Typography
                sx={{
                  color: theme.palette.text.neutralLightest,
                  textAlign: "center",
                  fontFamily: "Dosis",
                  textShadow: "0px 0px 12px rgba(18, 18, 18, 0.20)",
                  fontSize: "16px",
                  fontStyle: "normal",
                  fontWeight: 500,
                  lineHeight: "24px",
                  marginLeft: "8px",
                }}
              >
                Total Votes
              </Typography>
            </Box>

            <Typography
              sx={{
                color: theme.palette.text.neutralLightest,
                textAlign: "center",
                fontFamily: "Dosis",
                textShadow: "0px 0px 12px rgba(18, 18, 18, 0.20)",
                fontSize: "24px",
                fontStyle: "normal",
                fontWeight: 700,
                lineHeight: "28px",
                marginTop: "4px",
              }}
            >
              {winner?.votes}
            </Typography>
          </Box>

          <Box
            component="div"
            sx={{
              display: "flex",
              alignItems: "center",
              flexDirection: "column",
            }}
          >
            <Box
              component="div"
              sx={{
                display: "flex",
                alignItems: "center",
              }}
            >
              <img
                src={positionIcon}
                alt="Total Votes Icon"
                width="24"
                height="24"
              />
              <Typography
                sx={{
                  color: theme.palette.text.neutralLightest,
                  textAlign: "center",
                  fontFamily: "Dosis",
                  textShadow: "0px 0px 12px rgba(18, 18, 18, 0.20)",
                  fontSize: "16px",
                  fontStyle: "normal",
                  fontWeight: 500,
                  lineHeight: "24px",
                  marginLeft: "8px",
                }}
              >
                Position
              </Typography>
            </Box>

            <Typography
              sx={{
                color: theme.palette.text.neutralLightest,
                textAlign: "center",
                fontFamily: "Dosis",
                textShadow: "0px 0px 12px rgba(18, 18, 18, 0.20)",
                fontSize: "24px",
                fontStyle: "normal",
                fontWeight: 700,
                lineHeight: "28px",
                marginTop: "4px",
              }}
            >
              #1
            </Typography>
          </Box>
        </Box>
      </Box>
    </Box>
  );

  const NomineesList = () => (
    <TableContainer>
      <Table
        sx={{
          borderCollapse: "separate",
          borderSpacing: "0px 4px",
          width: "100%",
          maxWidth: "100%",
          tableLayout: "fixed",
        }}
      >
        <TableHead sx={{ background: "transparent" }}>
          <TableRow>
            <TableCell
              sx={{
                color: theme.palette.text.neutralLightest,
                textShadow: "0px 0px 12px rgba(18, 18, 18, 0.20)",
                textAlign: "center",
                fontSize: "12px",
                fontStyle: "normal",
                fontWeight: 700,
                lineHeight: "20px",
                border: "none",
                width: {
                  xs: "20%",
                  sm: "15%",
                },
              }}
            >
              Position
            </TableCell>
            <TableCell
              sx={{
                color: theme.palette.text.neutralLightest,
                textShadow: "0px 0px 12px rgba(18, 18, 18, 0.20)",
                fontSize: "12px",
                fontStyle: "normal",
                fontWeight: 700,
                lineHeight: "20px",
                border: "none",
                maxWidth: "137px",
                width: {
                  xs: "40%",
                  sm: "55%",
                },
              }}
            >
              Nominee
            </TableCell>
            <TableCell
              sx={{
                color: theme.palette.text.neutralLightest,
                textShadow: "0px 0px 12px rgba(18, 18, 18, 0.20)",
                fontSize: "12px",
                fontStyle: "normal",
                fontWeight: 700,
                lineHeight: "20px",
                border: "none",
                width: {
                  xs: "20%",
                },
              }}
            >
              Votes
            </TableCell>
            <TableCell
              sx={{
                color: theme.palette.text.neutralLightest,
                textShadow: "0px 0px 12px rgba(18, 18, 18, 0.20)",
                fontSize: "12px",
                fontStyle: "normal",
                fontWeight: 700,
                lineHeight: "20px",
                border: "none",
                width: {
                  xs: "10%",
                  sm: "10%",
                },
              }}
            ></TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {nominees?.map((nominee, index) => (
            <TableRow
              key={index}
              sx={{
                borderRadius: "8px",
                overflow: "hidden",
                height: "72px",
                border: " 1px solid var(--orange, EE9766)",
                cursor: "pointer",
              }}
            >
              <TableCell
                sx={{
                  background: theme.palette.background.neutralDark,
                  borderLeft:
                    votedFor === nominee.id
                      ? `1px solid ${theme.palette.secondary.main}`
                      : `1px solid transparent`,
                  borderTop:
                    votedFor === nominee.id
                      ? `1px solid ${theme.palette.secondary.main}`
                      : `1px solid transparent`,
                  borderBottom:
                    votedFor === nominee.id
                      ? `1px solid ${theme.palette.secondary.main}`
                      : `1px solid transparent`,
                  borderRight: "none",
                  borderTopLeftRadius: "20px",
                  borderBottomLeftRadius: "20px",
                  color: theme.palette.text.neutralLightest,
                  textAlign: "center",
                  fontFamily: "Dosis",
                  fontSize: "20px",
                  fontStyle: "normal",
                  fontWeight: 700,
                  lineHeight: "24px",
                  padding: "28px 24px",
                }}
              >
                <Box
                  component="div"
                  sx={{
                    display: "flex",
                    justifyContent: "center",
                    alignItems: "center",
                    width: "100%",
                  }}
                >
                  <img
                    src={positionIcon}
                    alt="Total Votes"
                    width="24"
                    height="24"
                  />
                  <Typography
                    sx={{
                      color: theme.palette.text.neutralLightest,
                      textAlign: "center",
                      fontFamily: "Dosis",
                      textShadow: "0px 0px 12px rgba(18, 18, 18, 0.20)",
                      fontSize: "20px",
                      fontStyle: "normal",
                      fontWeight: 700,
                      lineHeight: "24px",
                      marginLeft: "8px",
                    }}
                  >
                    #{index + 2}
                  </Typography>
                </Box>
              </TableCell>
              <TableCell
                sx={{
                  background: theme.palette.background.neutralDark,
                  borderTop:
                    votedFor === nominee.id
                      ? `1px solid ${theme.palette.secondary.main}`
                      : `1px solid transparent`,
                  borderBottom:
                    votedFor === nominee.id
                      ? `1px solid ${theme.palette.secondary.main}`
                      : `1px solid transparent`,
                  borderRight: "none",
                  maxWidth: "137px",
                  overflow: "hidden",
                  textOverflow: "ellipsis",
                  whiteSpace: "nowrap",
                }}
              >
                <Box
                  component="div"
                  sx={{
                    display: "flex",
                  }}
                >
                  <img
                    src={nomineeIcon}
                    alt="Total Votes"
                    width="24"
                    height="24"
                  />
                  <Typography
                    onClick={(
                      event: React.MouseEvent<HTMLDivElement, MouseEvent>,
                    ) => handleLearnMoreClick(event, nominee.id)}
                    sx={{
                      color: theme.palette.text.neutralLightest,
                      textAlign: "center",
                      fontFamily: "Dosis",
                      textShadow: "0px 0px 12px rgba(18, 18, 18, 0.20)",
                      fontSize: "20px",
                      fontStyle: "normal",
                      fontWeight: 700,
                      lineHeight: "24px",
                      marginLeft: "8px",
                      cursor: "pointer",
                    }}
                  >
                    {nominee.name}
                  </Typography>
                </Box>
              </TableCell>
              <TableCell
                sx={{
                  background: theme.palette.background.neutralDark,
                  borderTop:
                    votedFor === nominee.id
                      ? `1px solid ${theme.palette.secondary.main}`
                      : `1px solid transparent`,
                  borderBottom:
                    votedFor === nominee.id
                      ? `1px solid ${theme.palette.secondary.main}`
                      : `1px solid transparent`,
                  borderLeft: "none",
                }}
              >
                <Box
                  component="div"
                  sx={{
                    display: "flex",
                  }}
                >
                  <img
                    src={votesIcon}
                    alt="Total Votes"
                    width="24"
                    height="24"
                  />
                  <Typography
                    sx={{
                      color: theme.palette.text.neutralLightest,
                      textAlign: "center",
                      fontFamily: "Dosis",
                      textShadow: "0px 0px 12px rgba(18, 18, 18, 0.20)",
                      fontSize: "20px",
                      fontStyle: "normal",
                      fontWeight: 700,
                      lineHeight: "24px",
                      marginLeft: "8px",
                    }}
                  >
                    {nominee.votes}
                  </Typography>
                </Box>
              </TableCell>
              <TableCell
                sx={{
                  background: theme.palette.background.neutralDark,
                  borderTopRightRadius: "20px",
                  borderBottomRightRadius: "20px",
                  borderTop:
                    votedFor === nominee.id
                      ? `1px solid ${theme.palette.secondary.main}`
                      : `1px solid transparent`,
                  borderBottom:
                    votedFor === nominee.id
                      ? `1px solid ${theme.palette.secondary.main}`
                      : `1px solid transparent`,
                  borderRight:
                    votedFor === nominee.id
                      ? `1px solid ${theme.palette.secondary.main}`
                      : `1px solid transparent`,
                  borderLeft: "none",
                }}
              >
                <Box
                  component="div"
                  sx={{
                    width: "24px",
                  }}
                >
                  {votedFor === nominee.id ? (
                    <TickIcon circleSize={24} tickSize={16} />
                  ) : null}
                </Box>
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </TableContainer>
  );

  return (
    <>
      <Fade in={fadeChecked} timeout={200}>
        <Box
          component="div"
          sx={{
            display: "flex",
            flexDirection: { xs: "column", sm: "column", md: "row" },
            alignItems: { md: "flex-start" },
            overflowX: "hidden",
          }}
        >
          <Box
            component="div"
            sx={{
              flex: 1,
              marginBottom: { xs: 2, sm: 2 },
              mx: { xs: "auto", sm: "auto", md: 0 },
              maxWidth: { xs: "100%", md: "400px" },
              width: {
                xs: "100%",
                sm: "100%",
                md: "auto",
              },
            }}
          >
            <Winner />
          </Box>
          <Box
            component="div"
            sx={{
              flex: 2,
              width: "100%",
              mx: { xs: "auto", sm: "auto", md: 0 },
              paddingLeft: {
                xs: "0px",
                md: "24px",
              },
            }}
          >
            <NomineesList />
          </Box>
        </Box>
      </Fade>
    </>
  );
};

export { Winners };
