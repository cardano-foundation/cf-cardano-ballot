import React, { useEffect, useState } from "react";
import {
  Box, Container,
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
    eventBus.publish(EventName.ShowToast, "Copied to clipboard successfully.");
  };

  const ReceiptsList = () => (
    <TableContainer>
      <Table sx={{ borderCollapse: "separate", borderSpacing: "0 4px" }}>
        <TableHead sx={{ background: "transparent", padding: "8px 28px" }}>
          <TableRow sx={{ background: "transparent", padding: "8px 28px" }}>
            <TableCell
              sx={{
                color: theme.palette.text.primary,
                fontSize: "12px",
                fontStyle: "normal",
                fontWeight: 600,
                lineHeight: "20px",
                border: "none",
                padding: "8px 28px"
              }}
            >
              Voted For
            </TableCell>
            <TableCell
              sx={{
                color: theme.palette.text.primary,
                fontSize: "12px",
                fontStyle: "normal",
                fontWeight: 600,
                lineHeight: "20px",
                border: "none",
                padding: "8px 28px"
              }}
            >
              Category Name
            </TableCell>
            <TableCell
              sx={{
                color: theme.palette.text.primary,
                fontSize: "12px",
                fontStyle: "normal",
                fontWeight: 600,
                lineHeight: "20px",
                border: "none",
                padding: "8px 28px"
              }}
            >
              Voted at Slot
            </TableCell>
            <TableCell
              sx={{
                display: { xs: "none", tablet: "flex" },
                color: theme.palette.text.primary,
                fontSize: "12px",
                fontStyle: "normal",
                fontWeight: 600,
                lineHeight: "20px",
                border: "none",
                padding: "8px 28px"
              }}
            >
              Transaction Hash
            </TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {Object.keys(extendedReceipts).map((category: string, index) => (
            <TableRow
              onClick={() => handleReceiptClick(category)}
              key={index}
              sx={{
                borderRadius: "8px",
                overflow: "hidden",
                height: "72px",
                cursor: "pointer",
              }}
            >
              <TableCell
                sx={{
                  border: "none",
                  color: theme.palette.text.neutralLightest,
                  textAlign: "center",
                  fontSize: "16px",
                  fontStyle: "normal",
                  fontWeight: 600,
                  lineHeight: "24px",
                  padding: "20px 28px",
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
                      fontWeight: 600,
                    }}
                  >
                    {extendedReceipts[category].proposalName}
                  </Typography>
                </Box>
              </TableCell>
              <TableCell
                sx={{
                  border: "none",
                  color: theme.palette.text.neutralLightest,
                  textAlign: "center",
                  fontSize: "16px",
                  fontStyle: "normal",
                  fontWeight: 600,
                  lineHeight: "24px",
                  padding: "20px 28px",
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
                      fontWeight: 600,
                    }}
                  >
                    {extendedReceipts[category].categoryName}
                  </Typography>
                </Box>
              </TableCell>
              <TableCell
                sx={{
                  border: "none",
                  color: theme.palette.text.neutralLightest,
                  textAlign: "center",
                  fontSize: "16px",
                  fontStyle: "normal",
                  fontWeight: 600,
                  lineHeight: "24px",
                  padding: "20px 28px",
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
                      fontWeight: 600,
                    }}
                  >
                    {extendedReceipts[category].votedAtSlot}
                  </Typography>
                </Box>
              </TableCell>
              <TableCell
                sx={{
                  display: { xs: "none", tablet: "flex" },
                  border: "none",
                  color: theme.palette.text.neutralLightest,
                  textAlign: "center",
                  fontSize: "16px",
                  fontStyle: "normal",
                  fontWeight: 600,
                  lineHeight: "24px",
                  padding: "20px 28px",
                }}
              >
                <Box
                  component="div"
                  sx={{
                    display: "flex",
                    alignItems: "center",
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
                      fontWeight: 600,
                      marginLeft: "8px",
                    }}
                  >
                    {addressSlice(extendedReceipts[category].signature)}
                  </Typography>
                  <img
                    onClick={() => handleReceiptClick(category)}
                    src={rightArrowIcon}
                    alt="Total Votes"
                    width="24"
                    height="24"
                    style={{
                      cursor: "pointer",
                      marginLeft: "16px"
                    }}
                  />
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
        <Container>
          <Box
            component="div"
            sx={{
              height: "28px",
            }}
          />
          <Typography
            sx={{
              color: "#fff",
              fontSize: "44px",
              fontStyle: "normal",
              fontWeight: 500,
              fontFamily: "Tomorrow",
              lineHeight: "44px",
              marginTop: "24px",
              marginBottom: "44px",
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
        </Container>
      </PageBase>
    </>
  );
};

export { ReceiptHistory };
