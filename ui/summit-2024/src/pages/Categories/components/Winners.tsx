import React from "react";
import {
  Box,
  Fade,
  Icon,
  Paper,
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
import theme from "../../../common/styles/theme";

interface WinnersProps {
  fadeChecked: boolean;
  nominees: Proposal[];
  selectedNominee: string | undefined;
  handleSelectedNominee: (nomineeId: string) => void;
}

const Winners: React.FC<WinnersProps> = ({
  fadeChecked,
  nominees,
  selectedNominee,
  handleSelectedNominee,
}) => {
  const SectionA = () => (
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
          variant="subtitle1"
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
          }}
        >
          {nominees[0].id}
        </Typography>
        <Box
          component="div"
          sx={{
            display: "flex",
            marginTop: "20px",
          }}
        >
          <Box
            component="div"
            sx={{
              display: "flex",
              alignItems: "flex-start",
              flexDirection: "column",
              width: "160x",
            }}
          >
            <Icon />
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
              }}
            >
              Total Votes
            </Typography>
            <Typography
              sx={{
                color: theme.palette.text.neutralLightest,
                textAlign: "center",
                textShadow: "0px 0px 12px rgba(18, 18, 18, 0.20)",
                fontSize: "24px",
                fontStyle: "normal",
                fontWeight: 700,
                lineHeight: "28px",
                  marginTop: "4px"
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
              marginLeft: "102px",
              width: "160x",
            }}
          >
            <Icon />
            <Typography
              sx={{
                color: theme.palette.text.neutralLightest,
                textAlign: "center",
                textShadow: "0px 0px 12px rgba(18, 18, 18, 0.20)",
                fontSize: "16px",
                fontStyle: "normal",
                fontWeight: 500,
                lineHeight: "24px",
              }}
            >
              Position
            </Typography>
            <Typography
                sx={{
                    color: theme.palette.text.neutralLightest,
                    textAlign: "center",
                    textShadow: "0px 0px 12px rgba(18, 18, 18, 0.20)",
                    fontSize: "24px",
                    fontStyle: "normal",
                    fontWeight: 700,
                    lineHeight: "28px",
                    marginTop: "4px"
                }}
            >
              #1
            </Typography>
          </Box>
        </Box>
      </Box>
    </Box>
  );

  const SectionB = () => (
    <TableContainer component={Paper}>
      <Table>
        <TableHead>
          <TableRow>
            <TableCell>Position</TableCell>
            <TableCell>Nominee</TableCell>
            <TableCell>Votes</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {[...Array(10)].map((_, index) => (
            <TableRow key={index}>
              <TableCell>{index + 1}</TableCell>
              <TableCell>Nominee {index + 1}</TableCell>
              <TableCell>{Math.floor(Math.random() * 100)}</TableCell>
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
            <SectionA />
          </Box>
          <Box component="div" sx={{ flex: 2 }}>
            <SectionB />
          </Box>
        </Box>
      </Fade>
    </>
  );
};

export { Winners };
