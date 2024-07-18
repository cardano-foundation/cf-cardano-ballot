import React, { useState } from "react";
import { Box, Drawer } from "@mui/material";
import { PageBase } from "../BasePage";
import { ViewReceipt } from "../Categories/components/ViewReceipt";
import { STATE } from "../Categories/components/ViewReceipt.type";

const ReceiptHistory: React.FC = () => {
  const [openViewReceipt, setOpenViewReceipt] = useState(true);

  return (
    <>
      <PageBase title="Categories">
        <Box
          component="div"
          sx={{
            height: "28px",
          }}
        />

        <Drawer
          open={openViewReceipt}
          anchor="right"
          onClose={() => setOpenViewReceipt(false)}
        >
          <ViewReceipt
            state={STATE.ROLLBACK}
            close={() => setOpenViewReceipt(false)}
          />
        </Drawer>
      </PageBase>
    </>
  );
};

export { ReceiptHistory };
