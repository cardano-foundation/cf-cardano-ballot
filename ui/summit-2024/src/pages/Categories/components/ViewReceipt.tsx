import React from "react";
import { Box, List, ListItem, Paper, Tooltip, Typography } from "@mui/material";
import CloseIcon from "@mui/icons-material/Close";
import theme from "../../../common/styles/theme";
import RefreshIcon from "@mui/icons-material/Refresh";
import InfoIcon from "@mui/icons-material/Info";
import NotificationsNoneIcon from "@mui/icons-material/NotificationsNone";
import { STATE, ViewReceiptProps } from "./ViewReceipt.type";
import { CustomAccordion } from "../../../components/common/CustomAccordion/CustomAccordion";

const ViewReceipt: React.FC<ViewReceiptProps> = ({ state, close }) => {
  const getContent = () => {
    switch (state) {
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
          infoList: [
            {
              title: "Event",
              value: "Ambassador - Cardano Summit 2024",
              tooltip: "info",
            },
            {
              title: "Proposal",
              value: "Plutus Bear Pop-Tart",
              tooltip: "info",
            },
            {
              title: "Voting Power",
              value: "9,997k ADA",
              tooltip: "info",
            },
            {
              title: "Voter Staking Address",
              value: "stake123...456spyqyg890",
              tooltip: "info",
            },
            {
              title: "Status",
              value: "Ambassador - Cardano Summit 2024",
              tooltip: "info",
            },
            {
              title: "Event",
              value: state,
              tooltip: "info",
            },
          ],
          advancedInfo: [],
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
          sx={{
            margin: "28px",
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
              sx={{
                display: "flex",
                flexDirection: "row",
                justifyContent: "space-between",
                width: "100%",
                borderBottom: `1px solid ${theme.palette.background.disabled}`,
              }}
            >
              <Box
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
              sx={{
                display: "flex",
                alignItems: "center",
                alignSelf: "center",
                padding: "12px 24px",
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
            {content?.infoList.map((item) => {
              return (
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
                      width: "100%",
                      color: theme.palette.text.neutralLight,
                      fontSize: "12px",
                      fontWeight: 500,
                      lineHeight: "20px",
                      fontStyle: "normal",
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
          sx={{
            backgroundColor: theme.palette.background.default,
          }}
        >
          <Box
            sx={{
              mx: "28px",
              backgroundColor: theme.palette.background.default,
            }}
          >
            <CustomAccordion
              titleOpen="Hide Advanced Information"
              titleClose="Show Advanced Information"
              description="descccc"
              sx={{
                marginTop: "20px",
              }}
            >
              <List>
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
                    <Tooltip title="info" placement="top">
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
                    453241
                  </Typography>
                </ListItem>
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
                      ID
                    </Typography>
                    <Tooltip title="info" placement="top">
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
                    e51fdf09...4c836052b4f0
                  </Typography>
                </ListItem>
              </List>
            </CustomAccordion>
            <Box
              sx={{
                backgroundColor: theme.palette.background.default,
                height: "28px",
              }}
            />
          </Box>
        </Box>
      </div>
    </>
  );
};

export { ViewReceipt };
