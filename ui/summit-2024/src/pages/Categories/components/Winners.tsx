import React from "react";
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
import { Proposal } from "../../../store/reducers/eventCache/eventCache.types";
import awardImg from "../../../assets/awardWinner.svg";
import winnerBg from "../../../assets/bg/winnerBg.svg";
import votesIcon from "../../../assets/votesIcon.svg";
import positionIcon from "../../../assets/positionIcon.svg";
import nomineeIcon from "../../../assets/nomineeIcon.svg";
import tickIcon from "../../../assets/tickIcon.svg";
import theme from "../../../common/styles/theme";

interface WinnersProps {
  fadeChecked: boolean;
  nominees: Proposal[];
  selectedNominee: string | undefined;
  handleSelectedNominee: (nomineeId: string) => void;
  handleOpenLearnMore: (nomineeId: string) => void;
}

const Winners: React.FC<WinnersProps> = ({
  fadeChecked,
  nominees,
  selectedNominee,
  handleSelectedNominee,
  handleOpenLearnMore,
}) => {
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
        width: {
          xs: "auto",
          sm: "400px",
        },
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
          <TickIcon circleSize={28} tickSize={20} />
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
            handleLearnMoreClick(event, nominees[0].id)
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
          {nominees[0].id}
        </Typography>
        <Box
          component="div"
          sx={{
            display: "flex",
            marginTop: "40px",
          }}
        >
          <Box
            component="div"
            sx={{
              display: "flex",
              alignItems: "flex-start",
              flexDirection: "column",
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
                marginLeft: "32px",
              }}
            >
              100
            </Typography>
          </Box>

          <Box
            component="div"
            sx={{
              display: "flex",
              alignItems: "flex-start",
              flexDirection: "column",
              marginLeft: "92px",
            }}
          >
            <Box
              component="div"
              sx={{
                display: "flex",
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
                marginLeft: "32px",
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
                  xs: "20%",
                  sm: "5%",
                },
              }}
            ></TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {[...nominees, ...nominees, ...nominees].map((nominee, index) => (
            <TableRow
              onClick={() => handleSelectedNominee(nominee.id)}
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
                    selectedNominee === nominee.id
                      ? `1px solid ${theme.palette.secondary.main}`
                      : `1px solid transparent`,
                  borderTop:
                    selectedNominee === nominee.id
                      ? `1px solid ${theme.palette.secondary.main}`
                      : `1px solid transparent`,
                  borderBottom:
                    selectedNominee === nominee.id
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
                    #{index + 1}
                  </Typography>
                </Box>
              </TableCell>
              <TableCell
                sx={{
                  background: theme.palette.background.neutralDark,
                  borderTop:
                    selectedNominee === nominee.id
                      ? `1px solid ${theme.palette.secondary.main}`
                      : `1px solid transparent`,
                  borderBottom:
                    selectedNominee === nominee.id
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
                    {nominee.id}
                  </Typography>
                </Box>
              </TableCell>
              <TableCell
                sx={{
                  background: theme.palette.background.neutralDark,
                  borderTop:
                    selectedNominee === nominee.id
                      ? `1px solid ${theme.palette.secondary.main}`
                      : `1px solid transparent`,
                  borderBottom:
                    selectedNominee === nominee.id
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
                    100
                  </Typography>
                </Box>
              </TableCell>
              <TableCell
                sx={{
                  background: theme.palette.background.neutralDark,
                  borderTopRightRadius: "20px",
                  borderBottomRightRadius: "20px",
                  borderTop:
                    selectedNominee === nominee.id
                      ? `1px solid ${theme.palette.secondary.main}`
                      : `1px solid transparent`,
                  borderBottom:
                    selectedNominee === nominee.id
                      ? `1px solid ${theme.palette.secondary.main}`
                      : `1px solid transparent`,
                  borderRight:
                    selectedNominee === nominee.id
                      ? `1px solid ${theme.palette.secondary.main}`
                      : `1px solid transparent`,
                  borderLeft: "none",
                }}
              >
                <Box
                  component="div"
                  sx={{
                    display: "flex",
                    width: "24px",
                  }}
                >
                  {selectedNominee === nominee.id ? (
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
              maxWidth: { xs: "335px", sm: "400px" },
              marginBottom: { xs: 2, sm: 2 },
              mx: { xs: "auto", sm: "auto", md: 0 },
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
