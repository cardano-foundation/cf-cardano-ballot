import React, { useEffect, useState } from "react";
import {
  Box,
  Drawer,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Typography,
} from "@mui/material";
import { PageBase } from "../BasePage";
import { ViewReceipt } from "../Categories/components/ViewReceipt";
import theme from "../../common/styles/theme";
import nomineeIcon from "../../assets/nomineeIcon.svg";
import rightArrowIcon from "../../assets/rightArrowIcon.svg";
import ContentCopyIcon from "@mui/icons-material/ContentCopy";
import { addressSlice, copyToClipboard } from "../../utils/utils";
import { eventBus, EventName } from "../../utils/EventBus";
import { getUserInSession, tokenIsExpired } from "../../utils/session";
import { getVoteReceipts } from "../../common/api/voteService";
import { useAppDispatch, useAppSelector } from "../../store/hooks";
import { getReceipts, setVoteReceipts } from "../../store/reducers/votesCache";
import { getEventCache } from "../../store/reducers/eventCache";
import { ExtendedVoteReceipt } from "../../types/voting-app-types";

const ReceiptHistory: React.FC = () => {
  const [selectedCategory, setSelectedCategory] = useState("");
  const [openViewReceipt, setOpenViewReceipt] = useState(false);
  let receipts = useAppSelector(getReceipts);
  const eventCache = useAppSelector(getEventCache);

  const session = getUserInSession();
  const dispatch = useAppDispatch();

  // @ts-ignore
  const extendedReceipts: ExtendedVoteReceipt = { ...receipts };
  for (const categoryKey in extendedReceipts) {
    const votingCategory = extendedReceipts[categoryKey];
    const categoryDetail = eventCache.categories.find(
      (category) => category.id === votingCategory.category,
    );

    if (categoryDetail) {
      const proposalDetail = categoryDetail.proposals.find(
        (proposal) => proposal.id === votingCategory.proposal,
      );

      if (proposalDetail) {
        extendedReceipts[categoryKey] = {
          ...votingCategory,
          categoryName: categoryDetail.name,
          proposalName: proposalDetail.name,
        };
      }
    }
  }

  useEffect(() => {
    if (!tokenIsExpired(session?.expiresAt)) {
      getVoteReceipts(session?.accessToken).then((receipts) => {
        // @ts-ignore
        dispatch(setVoteReceipts(receipts));
      });
    }
  }, []);

  const handleReceiptClick = (cat: string) => {
    setOpenViewReceipt(true);
    setSelectedCategory(cat);
    if (!tokenIsExpired(session?.expiresAt)) {
      getVoteReceipts(session?.accessToken).then((receipts) => {
        // @ts-ignore
        dispatch(setVoteReceipts(receipts));
      });
    }
  };

  const handleCopy = async (transactionId: string) => {
    await copyToClipboard(transactionId);
    eventBus.publish(EventName.ShowToast, "Copied to clipboard successfully");
  };

  const ReceiptsList = () => (
    <TableContainer>
      <Table sx={{ borderCollapse: "separate", borderSpacing: "0 4px" }}>
        <TableHead sx={{ background: "transparent" }}>
          <TableRow>
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
              Voted For
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
              Category Name
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
              Voted At Slot
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
              Signature
            </TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {Object.keys(extendedReceipts).map((category: string, index) => (
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
                    src={nomineeIcon}
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
                    {extendedReceipts[category].proposalName}
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
                      cursor: "pointer",
                    }}
                  >
                    {extendedReceipts[category].categoryName}
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
                    {extendedReceipts[category].votedAtSlot}
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
                  <ContentCopyIcon
                    onClick={() =>
                      handleCopy(extendedReceipts[category].signature)
                    }
                    sx={{
                      width: "20px",
                      height: "20px",
                      cursor: "pointer",
                    }}
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
                    {addressSlice(extendedReceipts[category].signature)}
                  </Typography>
                  <Box
                    component="div"
                    sx={{
                      marginLeft: "auto",
                      marginRight: "28px",
                    }}
                  >
                    <img
                      onClick={() => handleReceiptClick(category)}
                      src={rightArrowIcon}
                      alt="Total Votes"
                      width="24"
                      height="24"
                      style={{
                        cursor: "pointer",
                      }}
                    />
                  </Box>
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
      <PageBase title="Categories">
        <Box
          component="div"
          sx={{
            height: "28px",
          }}
        />
        <Typography
          sx={{
            color: theme.palette.text.neutralLightest,
            fontSize: "32px",
            fontStyle: "normal",
            fontWeight: 700,
            fontFamily: "Dosis",
            lineHeight: "36px",
            marginTop: "60px",
            marginBottom: "32px",
          }}
        >
          Vote Receipt History
        </Typography>
        <ReceiptsList />
        <Drawer
          open={openViewReceipt}
          anchor="right"
          onClose={() => setOpenViewReceipt(false)}
        >
          <ViewReceipt
            categoryId={selectedCategory}
            close={() => setOpenViewReceipt(false)}
          />
        </Drawer>
      </PageBase>
    </>
  );
};

export { ReceiptHistory };
