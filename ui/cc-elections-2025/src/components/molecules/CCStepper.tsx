import { styled } from '@mui/material/styles';
import Box from "@mui/material/Box";
import Stepper from "@mui/material/Stepper";
import Step from '@mui/material/Step';
import StepLabel, { stepLabelClasses } from '@mui/material/StepLabel';
import StepConnector, { stepConnectorClasses } from '@mui/material/StepConnector';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import RadioButtonCheckedIcon from '@mui/icons-material/RadioButtonChecked';
import RadioButtonUncheckedIcon from '@mui/icons-material/RadioButtonUnchecked';
import { StepIconProps } from '@mui/material/StepIcon';

type CCStepperProps = {
  activeStep: number;
  steps: string[];
};

const CCStepLabel = styled(StepLabel)(() => ({
  [`& .${stepLabelClasses.label}.${stepLabelClasses.alternativeLabel}`]: {
    marginTop: '4px',
    fontSize: '12px',
    fontWeight: 400,
    color: '#506288',
  },
  [`& .${stepLabelClasses.label}.${stepLabelClasses.active}`]: {
    marginTop: '4px',
    color: '#3052F5',
  },
}));

const CCStepConnector = styled(StepConnector)(({ theme }) => ({
  [`&.${stepConnectorClasses.alternativeLabel}`]: {
    top: 10,
    left: 'calc(-50% + 11px)',
    right: 'calc(50% + 11px)',
  },
  [`&.${stepConnectorClasses.active}`]: {
    [`& .${stepConnectorClasses.line}`]: {
      borderColor: '#784af4',
    },
  },
  [`&.${stepConnectorClasses.completed}`]: {
    [`& .${stepConnectorClasses.line}`]: {
      borderColor: '#784af4',
    },
  },
  [`& .${stepConnectorClasses.line}`]: {
    borderColor: '#D6D8FF',
    borderTopWidth: 2,
    ...theme.applyStyles('dark', {
      borderColor: theme.palette.grey[800],
    }),
  },
}));

function CCStepIcon(props: StepIconProps) {
  const { active, completed } = props;

  return (
    <>
      {completed ? (
        <CheckCircleIcon sx={{ color: '#3052F5' }} />
      ) : active ? (
        <RadioButtonCheckedIcon sx={{ color: '#3052F5' }} />
      ) : (
        <RadioButtonUncheckedIcon sx={{ color: '#D6D8FF' }} />
      )}
    </>
  );
}

export const CCStepper = (props: CCStepperProps) => {
  const { steps, activeStep } = props;
  return (
    <Box sx={{ width: '100%' }}>
      <Stepper alternativeLabel activeStep={activeStep} connector={<CCStepConnector />}>
        {steps.map((label: string) => (
          <Step key={label}>
            <CCStepLabel StepIconComponent={CCStepIcon}>{label}</CCStepLabel>
          </Step>
        ))}
      </Stepper>
    </Box>
  );
}
