import Box from "@mui/material/Box";
import Typography from "@mui/material/Typography";
import { Button } from "../atoms";

import styles from './ApplyCard.module.scss';

type ApplyCardProps = {
  title: string;
  subTitle: string;
  iconUrl: string;
  handleClick: (event: React.MouseEvent) => void;
}

export const ApplyCard = (props: ApplyCardProps) => {

  const handleButtonClick = (event: React.MouseEvent) => {
    props.handleClick(event);
  }

  return (
    <Box className={styles.container}>
      <img src={props.iconUrl} alt="user" height={40} />
      <Box sx={{ width: '310px' }}>
        <Typography variant="h3">{props.title}</Typography>
        <Typography variant="body2">{props.subTitle}</Typography>
      </Box>
      <Button onClick={handleButtonClick}>Apply</Button>
    </Box>
  );
}
