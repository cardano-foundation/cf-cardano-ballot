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
import theme from "../../../common/styles/theme";

interface WinnersProps {
  fadeChecked: boolean;
  nominees: Proposal[];
  handleOpenLearnMore: (nomineeId: string) => void;
}

const Winners: React.FC<WinnersProps> = ({
  fadeChecked,
  nominees,
  handleOpenLearnMore,
}) => {
  const handleLearnMoreClick = (nomineeId: string) => {
    handleOpenLearnMore(nomineeId);
  };
  const Winner = () => (
    <Box
      component="div"
      sx={{
        width: {
          xs: "auto",
          sm: "400px",
        },
        position: "relative",
        borderRadius: "24px",
        overflow: "hidden",
        padding: "4px",
        background:
          "linear-gradient(45deg, #0C7BC5 0%, #40407D 50%, #EE9766 100%)",
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
          padding: "44px 28px",
        }}
      >
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
          <img src={awardImg} alt="Placeholder" />
        </Box>
        <Typography
          onClick={() => handleLearnMoreClick(nominees[0].id)}
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
      <Table sx={{ borderCollapse: "separate", borderSpacing: "0 4px" }}>
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
              }}
            >
              Votes
            </TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {nominees.map((nominee, index) => (
            <TableRow
              key={index}
              sx={{
                borderRadius: "8px",
                overflow: "hidden",
                height: "72px",
              }}
            >
              <TableCell
                sx={{
                  background: theme.palette.background.neutralDark,
                  border: "none",
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
                  border: "none",
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
                    onClick={() => handleLearnMoreClick(nominee.id)}
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
                  border: "none",
                  borderTopRightRadius: "20px",
                  borderBottomRightRadius: "20px",
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
            flexDirection: { xs: "column", sm: "row" },
          }}
        >
          <Box
            component="div"
            sx={{
              flex: 1,
              marginRight: { sm: 2, xs: 0 },
              marginBottom: { sm: 0, xs: 2 },
            }}
          >
            <Winner />
          </Box>
          <Box component="div" sx={{ flex: 2 }}>
            <NomineesList />
          </Box>
        </Box>
      </Fade>
    </>
  );
};

export { Winners };
