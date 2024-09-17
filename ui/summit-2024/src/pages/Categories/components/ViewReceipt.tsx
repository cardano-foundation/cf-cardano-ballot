import React from "react";
import { Box, List, ListItem, Paper, Tooltip, Typography } from "@mui/material";
import CloseIcon from "@mui/icons-material/Close";
import theme from "../../../common/styles/theme";
import RefreshIcon from "@mui/icons-material/Refresh";
import InfoIcon from "@mui/icons-material/Info";
import NotificationsNoneIcon from "@mui/icons-material/NotificationsNone";
import WarningAmberIcon from "@mui/icons-material/WarningAmber";
import ErrorOutlineIcon from "@mui/icons-material/ErrorOutline";
import ArrowDownwardIcon from "@mui/icons-material/ArrowDownward";
import ArrowForwardOutlinedIcon from "@mui/icons-material/ArrowForwardOutlined";
import CheckCircleOutlineOutlinedIcon from "@mui/icons-material/CheckCircleOutlineOutlined";
import LinkOutlinedIcon from '@mui/icons-material/LinkOutlined';
import ArrowUpwardOutlinedIcon from "@mui/icons-material/ArrowUpwardOutlined";
import { STATE, ViewReceiptProps } from "./ViewReceipt.type";
import { CustomAccordion } from "../../../components/common/CustomAccordion/CustomAccordion";
import { JsonView } from "../../../components/common/JsonView/JsonView";
import { useAppSelector } from "../../../store/hooks";
import {
  getReceipts,
  setVoteReceipt,
  setVotes,
} from "../../../store/reducers/votesCache";
import { copyToClipboard } from "../../../utils/utils";
import { getUserInSession, tokenIsExpired } from "../../../utils/session";
import {
  getVoteReceipt,
  submitGetUserVotes,
} from "../../../common/api/voteService";
import { eventBus, EventName } from "../../../utils/EventBus";
import { ToastType } from "../../../components/common/Toast/Toast.types";
import { parseError } from "../../../common/constants/errors";
import { verifyVote } from "../../../common/api/verificationService";

const ViewReceipt: React.FC<ViewReceiptProps> = ({ categoryId, close }) => {
  const session = getUserInSession();
  const receipts = useAppSelector(getReceipts);
  const receipt = receipts[categoryId];

  const handleCopy = async (data: string) => {
    await copyToClipboard(data);
    eventBus.publish(EventName.ShowToast, "Copied to clipboard successfully");
  };

  const verifyVoteProof = async () => {
    if (receipt) {
      const body = {
        rootHash: receipt.merkleProof.rootHash,
        steps: receipt.merkleProof.steps,
        payload: receipt.payload,
        walletId: receipt.walletId,
        signature: receipt.signature,
        publicKey: receipt.publicKey
      };

      verifyVote(body)
        .then((result) => {
          if ("verified" in result && result.verified) {
            eventBus.publish(EventName.ShowToast, "Vote verified successfully");
          } else {
            eventBus.publish(
              EventName.ShowToast,
              "Vote no verified",
              ToastType.Error,
            );
          }
        })
        .catch((e) => {
          eventBus.publish(
            EventName.ShowToast,
            parseError(e.message),
            ToastType.Error,
          );
        });
    }
  };

  const viewOnChainVote = () => {
      if (receipt?.merkleProof?.transactionHash) {
          window.open(`https://preprod.cardanoscan.io/transaction/${receipt?.merkleProof?.transactionHash}` , "_blank");
      }
  }

  const refreshReceipt = () => {
    if (session && !tokenIsExpired(session?.expiresAt)) {
      // @ts-ignore
      getVoteReceipt(categoryId, session?.accessToken)
        .then((r) => {
          // @ts-ignore
          if (r.error) {
            // @ts-ignore
            eventBus.publish(EventName.ShowToast, r.message, ToastType.Error);
            return;
          }
          if (JSON.stringify(r) === JSON.stringify(receipts[categoryId])) {
            eventBus.publish(
              EventName.ShowToast,
              "No changes detected in the receipt",
            );
          } else {
            eventBus.publish(
              EventName.ShowToast,
              "Receipt updated successfully!",
            );
            // @ts-ignore
            dispatch(setVoteReceipt({ categoryId: categoryId, receipt: r }));
          }
        })
        .catch((e) => {
          if (process.env.NODE_ENV === "development") {
            console.log(
              `Failed to fetch vote receipt, ${parseError(e.message)}`,
            );
          }
        });
      submitGetUserVotes(session?.accessToken)
        .then((response) => {
          if (response) {
            // @ts-ignore
            dispatch(setVotes(response));
          }
        })
        .catch((e) => {
          if (process.env.NODE_ENV === "development") {
            console.log(`Failed to fetch user votes, ${parseError(e.message)}`);
          }
        });
    } else {
      eventBus.publish(
        EventName.OpenLoginModal,
        "Login to see your vote receipt.",
      );
    }
  };
  const getContent = () => {
    switch (receipt?.status) {
      case STATE.BASIC: {
        return {
          leftIcon: (
            <NotificationsNoneIcon
              sx={{
                width: "24px",
                height: "24px",
              }}
            />
          ),
          title: "Vote Not Ready for Verification",
          description:
            "Although your vote has been successfully submitted, you may have to wait up to 30 minutes for this to be visible on chain. Please check back later.",
          iconBottom: (
            <RefreshIcon
              sx={{
                cursor: "pointer",
                width: "16px",
                height: "16px",
              }}
            />
          ),
          labelBottom: "Refresh Status",
          iconBottomAction: refreshReceipt,
          infoList: [
            {
              title: "Category",
              value: receipt?.category,
              tooltip: "Category of the voting proposal.",
            },
            {
              title: "Proposal",
              value: receipt?.proposal,
              tooltip: "Title of the proposal.",
            },
            {
              title: "User Address",
              // @ts-ignore
              value: receipt?.walletId,
              tooltip: "Wallet address of the user who voted.",
            },
            {
              title: "Status",
              value: receipt?.status,
              tooltip: "Current status of the vote.",
            },
            {
              title: "Event",
              value: receipt?.event,
              tooltip: "Specific event related to the voting.",
            },
          ],
        };
      }
      case STATE.PARTIAL: {
        return {
          leftIcon: (
            <WarningAmberIcon
              sx={{
                width: "24px",
                height: "24px",
                color: "#EE9766",
              }}
            />
          ),
          title: "In Progress",
          description:
            "Your transaction has been sent and is awaiting confirmation from the Cardano network (this could be 5-10 minutes). Once this has been confirmed you’ll be able to verify your vote.",
          iconBottom: (
            <RefreshIcon
              sx={{
                cursor: "pointer",
                width: "16px",
                height: "16px",
              }}
            />
          ),
          labelBottom: "Refresh Status",
          iconBottomAction: refreshReceipt,
          infoList: [
            {
              title: "Category",
              value: receipt?.category,
              tooltip: "Category of the voting proposal.",
            },
            {
              title: "Proposal",
              value: receipt?.proposal,
              tooltip: "Title of the proposal.",
            },
            {
              title: "User Address",
              // @ts-ignore
              value: receipt?.walletId,
              tooltip: "Wallet address of the user who voted.",
            },
            {
              title: "Status",
              value: receipt?.status,
              tooltip: "Current status of the vote.",
            },
            {
              title: "Event",
              value: receipt?.event,
              tooltip: "Specific event related to the voting.",
            },
          ],
        };
      }
      case STATE.ROLLBACK: {
        return {
          leftIcon: (
            <ErrorOutlineIcon
              sx={{
                width: "24px",
                height: "24px",
                // @ts-ignore
                color: theme.palette.error.text,
              }}
            />
          ),
          title: "In Progress",
          description:
            "Your transaction has been sent and is awaiting confirmation from the Cardano network (this could be 5-10 minutes). Once this has been confirmed you’ll be able to verify your vote.",
          iconBottom: (
            <RefreshIcon
              sx={{
                cursor: "pointer",
                width: "16px",
                height: "16px",
              }}
            />
          ),
          labelBottom: "Refresh Status",
          iconBottomAction: refreshReceipt,
          infoList: [
            {
              title: "Category",
              value: receipt?.category,
              tooltip: "Category of the voting proposal.",
            },
            {
              title: "Proposal",
              value: receipt?.proposal,
              tooltip: "Title of the proposal.",
            },
            {
              title: "User Address",
              // @ts-ignore
              value: receipt?.walletId,
              tooltip: "Wallet address of the user who voted.",
            },
            {
              title: "Status",
              value: receipt?.status,
              tooltip: "Current status of the vote.",
            },
            {
              title: "Event",
              value: receipt?.event,
              tooltip: "Specific event related to the voting.",
            },
          ],
        };
      }
      case STATE.FULL: {
        const statusDescription = (() => {
          switch (receipt?.finalityScore) {
            case "LOW":
              return {
                description:
                  "Your vote is currently being verified. While in LOW, there is the highest chance of a rollback. Check back later to see if verification has completed.",
                icon: <ArrowDownwardIcon />,
              };
            case "MEDIUM":
              return {
                description:
                  "Your vote is currently being verified. While in MEDIUM, the chance of rollback is still possible. Check back later to see if verification has completed.",
                icon: (
                  <ArrowForwardOutlinedIcon
                    sx={{
                      width: "24px",
                      height: "24px",
                      color: "#EE9766",
                    }}
                  />
                ),
              };
            case "HIGH":
              return {
                description:
                  "Your vote is currently being verified. While in HIGH, the chance of a rollback is very unlikely. Check back later to see if verification has completed.",
                icon: (
                  <ArrowUpwardOutlinedIcon
                    sx={{
                      width: "24px",
                      height: "24px",
                      color: "#6EBE78",
                    }}
                  />
                ),
              };

            case "FINAL":
              return {
                description: "Your vote has been successfully verified.",
                icon: (
                  <CheckCircleOutlineOutlinedIcon
                    sx={{
                      width: "24px",
                      height: "24px",
                      color: "#6EBE78",
                    }}
                  />
                ),
              };
            default:
              return {
                description:
                  "Check back later to see if verification has completed.",
              };
          }
        })();

        const actionButton = receipt?.finalityScore === "FINAL" ? {
            action: viewOnChainVote,
            iconBottom: <LinkOutlinedIcon />,
            labelBottom: "View On Chain Vote"
        } : {
            action: refreshReceipt,
            iconBottom: <RefreshIcon
                sx={{
                    cursor: "pointer",
                    width: "16px",
                    height: "16px",
                }} />,
            labelBottom: "Refresh Status"
        }
        return {
          leftIcon: statusDescription.icon,
          title: "Assurance",
          description: statusDescription.description,
          labelBottom: actionButton.labelBottom,
          iconBottom: actionButton.iconBottom,
          iconBottomAction: actionButton.action ,
          infoList: [
            {
              title: "Category",
              value: receipt?.category,
              tooltip: "Category of the voting proposal.",
            },
            {
              title: "Proposal",
              value: receipt?.proposal,
              tooltip: "Title of the proposal.",
            },
            {
              title: "User Address",
              value: receipt?.walletId,
              tooltip: "Wallet address of the user who voted.",
            },
            {
              title: "Status",
              value: receipt?.status,
              tooltip: "Current status of the vote.",
            },
            {
              title: "Event",
              value: receipt?.event,
              tooltip: "Specific event related to the voting.",
            },
          ],
        };
      }
      default:
        return;
    }
  };
  const content = getContent();

  return (
    <>
      <div
        style={{
          backgroundColor: theme.palette.background.default,
          height: "100%",
        }}
      >
        <Box
          component="div"
          sx={{
            display: "flex",
            justifyContent: "center",
            alignItems: "center",
            padding: 1,
            width: "450px",
          }}
        >
          <Typography
            sx={{
              flexGrow: 1,
              color: theme.palette.text.neutralLightest,
              textAlign: "center",
              fontFamily: "Dosis",
              fontSize: "28px",
              fontWeight: 700,
              lineHeight: "32px",
              marginTop: "28px",
            }}
          >
            Vote Receipt
          </Typography>
          <Box
            component="div"
            onClick={() => close()}
            sx={{
              display: "inline-flex",
              padding: "12px",
              borderRadius: "12px",
              background: theme.palette.background.neutralDark,
              cursor: "pointer",
              "&:hover": {
                backgroundColor: theme.palette.text.neutralLightest,
                color: theme.palette.background.neutralDark,
              },
            }}
          >
            <CloseIcon />
          </Box>
        </Box>
        <Box
          component="div"
          sx={{
            mx: "28px",
          }}
        >
          <Paper
            sx={{
              display: "flex",
              flexDirection: "column",
              alignItems: "center",
              alignSelf: "stretch",
              color: theme.palette.text.neutralLight,
              fontFamily: "Roboto",
              fontSize: "12px",
              fontWeight: 500,
              lineHeight: "20px",
              borderRadius: "12px",
              background: theme.palette.background.neutralDark,
            }}
          >
            <Box
              component="div"
              sx={{
                display: "flex",
                flexDirection: "row",
                justifyContent: "space-between",
                width: "100%",
                borderBottom: `1px solid ${theme.palette.background.disabled}`,
              }}
            >
              <Box
                component="div"
                sx={{
                  display: "flex",
                  justifyContent: "center",
                  alignItems: "center",
                  width: "60px",
                  borderRight: `1px solid ${theme.palette.background.disabled}`,
                }}
              >
                {content?.leftIcon}
              </Box>

              <Box
                component="div"
                sx={{
                  width: "305px",
                  display: "flex",
                  flexDirection: "column",
                  justifyContent: "space-between",
                  my: "20px",
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
                  {content?.title}
                </Typography>
                <Typography
                  sx={{
                    color: theme.palette.text.neutralLight,
                    fontSize: "12px",
                    fontStyle: "normal",
                    fontWeight: 500,
                    lineHeight: "20px",
                    marginRight: "12px",
                    marginTop: "4px",
                  }}
                >
                  {content?.description}
                </Typography>
              </Box>
            </Box>
            <Box
              onClick={() => content?.iconBottomAction()}
              component="div"
              sx={{
                display: "flex",
                alignItems: "center",
                alignSelf: "center",
                padding: "12px 24px",
                cursor: "pointer",
              }}
            >
              {content?.iconBottom}
              <Typography
                sx={{
                  marginLeft: 1,
                  color: theme.palette.text.neutralLight,
                  fontSize: "12px",
                  fontStyle: "normal",
                  fontWeight: 500,
                  lineHeight: "20px",
                  cursor: "pointer",
                }}
              >
                {content?.labelBottom}
              </Typography>
            </Box>
          </Paper>

          <List>
            {content?.infoList?.map((item) => {
              return (
                <ListItem
                  onClick={() => handleCopy(item.value)}
                  sx={{
                    display: "flex",
                    width: "394px",
                    padding: "12px 16px",
                    flexDirection: "column",
                    alignItems: "flex-start",
                    borderRadius: "12px",
                    border: `1px solid ${theme.palette.background.darker}`,
                    background: theme.palette.background.default,
                    marginTop: "8px",
                    cursor: "pointer",
                  }}
                >
                  <Box
                    component="div"
                    sx={{
                      display: "flex",
                      width: "100%",
                      justifyContent: "space-between",
                      alignItems: "center",
                    }}
                  >
                    <Typography
                      sx={{
                        color: theme.palette.text.neutralLightest,
                        fontSize: "16px",
                        fontWeight: 500,
                        lineHeight: "24px",
                        fontStyle: "normal",
                      }}
                    >
                      {item.title}
                    </Typography>
                    <Tooltip title={item.tooltip} placement="top">
                      <InfoIcon
                        sx={{
                          cursor: "pointer",
                        }}
                      />
                    </Tooltip>
                  </Box>
                  <Typography
                    sx={{
                      width: "90%",
                      color: theme.palette.text.neutralLight,
                      fontSize: "12px",
                      fontWeight: 500,
                      lineHeight: "20px",
                      fontStyle: "normal",
                      whiteSpace: "nowrap",
                      overflow: "hidden",
                      textOverflow: "ellipsis",
                    }}
                  >
                    {item.value}
                  </Typography>
                </ListItem>
              );
            })}
          </List>
        </Box>
        <Box
          component="div"
          sx={{
            backgroundColor: theme.palette.background.default,
            marginTop: "10px",
          }}
        >
          <Box
            component="div"
            sx={{
              mx: "28px",
              backgroundColor: theme.palette.background.default,
            }}
          >
            <CustomAccordion
              titleOpen="Hide Advanced Information"
              titleClose="Show Advanced Information"
            >
              <List>
                <ListItem
                  onClick={() => handleCopy(receipt?.votedAtSlot)}
                  sx={{
                    display: "flex",
                    width: "394px",
                    padding: "12px 16px",
                    flexDirection: "column",
                    alignItems: "flex-start",
                    borderRadius: "12px",
                    border: `1px solid ${theme.palette.background.darker}`,
                    background: theme.palette.background.default,
                    marginTop: "8px",
                  }}
                >
                  <Box
                    component="div"
                    sx={{
                      display: "flex",
                      width: "100%",
                      justifyContent: "space-between",
                      alignItems: "center",
                    }}
                  >
                    <Typography
                      sx={{
                        color: theme.palette.text.neutralLightest,
                        fontSize: "16px",
                        fontWeight: 500,
                        lineHeight: "24px",
                        fontStyle: "normal",
                      }}
                    >
                      Voted at Slot
                    </Typography>
                    <Tooltip
                      title="Blockchain slot when the vote was cast."
                      placement="top"
                    >
                      <InfoIcon
                        sx={{
                          cursor: "pointer",
                        }}
                      />
                    </Tooltip>
                  </Box>
                  <Typography
                    sx={{
                      width: "100%",
                      color: theme.palette.text.neutralLight,
                      fontSize: "12px",
                      fontWeight: 500,
                      lineHeight: "20px",
                      fontStyle: "normal",
                    }}
                  >
                    {receipt?.votedAtSlot}
                  </Typography>
                </ListItem>
                <ListItem
                  onClick={() => handleCopy(receipt?.signature)}
                  sx={{
                    display: "flex",
                    width: "394px",
                    padding: "12px 16px",
                    flexDirection: "column",
                    alignItems: "flex-start",
                    borderRadius: "12px",
                    border: `1px solid ${theme.palette.background.darker}`,
                    background: theme.palette.background.default,
                    marginTop: "8px",
                  }}
                >
                  <Box
                    component="div"
                    sx={{
                      display: "flex",
                      width: "100%",
                      justifyContent: "space-between",
                      alignItems: "center",
                    }}
                  >
                    <Typography
                      sx={{
                        color: theme.palette.text.neutralLightest,
                        fontSize: "16px",
                        fontWeight: 500,
                        lineHeight: "24px",
                        fontStyle: "normal",
                      }}
                    >
                      Signature
                    </Typography>
                    <Tooltip
                      title="Digital signature verifying the voter's identity."
                      placement="top"
                    >
                      <InfoIcon
                        sx={{
                          cursor: "pointer",
                        }}
                      />
                    </Tooltip>
                  </Box>
                  <Typography
                    sx={{
                      width: "90%",
                      color: theme.palette.text.neutralLight,
                      fontSize: "12px",
                      fontWeight: 500,
                      lineHeight: "20px",
                      fontStyle: "normal",
                      whiteSpace: "nowrap",
                      overflow: "hidden",
                      textOverflow: "ellipsis",
                    }}
                  >
                    {/*@ts-ignore */}
                    {receipt?.signature}
                  </Typography>
                </ListItem>
                {
                  // @ts-ignore
                  receipt?.merkleProof ? (
                    <ListItem
                      sx={{
                        display: "flex",
                        width: "394px",
                        padding: "12px 16px",
                        flexDirection: "column",
                        alignItems: "flex-start",
                        borderRadius: "12px",
                        border: `1px solid ${theme.palette.background.darker}`,
                        background: theme.palette.background.default,
                        marginTop: "8px",
                      }}
                    >
                      <Box
                        component="div"
                        sx={{
                          display: "flex",
                          width: "100%",
                          justifyContent: "space-between",
                          alignItems: "center",
                        }}
                      >
                        <Typography
                          sx={{
                            color: theme.palette.text.neutralLightest,
                            fontSize: "16px",
                            fontWeight: 500,
                            lineHeight: "24px",
                            fontStyle: "normal",
                          }}
                        >
                          Vote Proof
                        </Typography>
                        <Tooltip
                          title="Data content of the voting transaction."
                          placement="top"
                        >
                          <InfoIcon
                            sx={{
                              cursor: "pointer",
                            }}
                          />
                        </Tooltip>
                      </Box>
                      <JsonView
                        data={JSON.stringify(
                          // @ts-ignore
                          receipt?.merkleProof,
                          null,
                          2,
                        )}
                        sx={{
                          marginTop: "10px",
                        }}
                        verifyProof={() => verifyVoteProof()}
                      />
                    </ListItem>
                  ) : null
                }
              </List>
            </CustomAccordion>
          </Box>
        </Box>
      </div>
    </>
  );
};

export { ViewReceipt };
