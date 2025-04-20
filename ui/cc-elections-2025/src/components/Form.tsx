import Box from "@mui/material/Box";
import {useNavigate} from "react-router-dom";
import {TopNav} from "./TopNav.tsx";
import Typography from "@mui/material/Typography";
import {Layout} from "./Layout/Layout.tsx";
import styles from "./molecules/FormCard.module.scss";
import {Button} from "./atoms";
import {ICONS} from "../consts";

import { FormStep1 } from "./IndividualFormSteps/FormStep1.tsx";
import { FormStep2 } from "./IndividualFormSteps/FormStep2.tsx";
import { FormStep3 } from "./IndividualFormSteps/FormStep3.tsx";
import { FormStep4 } from "./IndividualFormSteps/FormStep4.tsx";
import { FormStep5 } from "./IndividualFormSteps/FormStep5.tsx";
import { FormStep6 } from "./IndividualFormSteps/FormStep6.tsx";
import { FormStep7 } from "./IndividualFormSteps/FormStep7.tsx";

import type { IndividualFormData, FormContextType } from '../types/formData';
import {useContext} from "react";
import { FormContext } from "@context";
import {usePostIndividual} from "@hooks";
import { CandidateBody } from "@models";

type IndividualFormDataProps = keyof IndividualFormData;

type FormErrors = {
  termsAndCondition?: boolean;
  coldCredential?: boolean;
  name?: boolean;
  email?: boolean;
};

export const Form = () => {

  const {
    page,
    setPage,
    data,
    title,
    req,
    setError
  } = useContext(FormContext) as FormContextType<IndividualFormData>;

  const { mutate, isLoading } = usePostIndividual()

  const display = [
    <FormStep1 />,
    <FormStep2 />,
    <FormStep3 />,
    <FormStep4 />,
    <FormStep5 />,
    <FormStep6 />,
    <FormStep7 />,
  ];

  const navigate = useNavigate();

  const isSubmit = page === title.length - 1;

  const handleBack = () => {
    if(page === 0) {
      navigate("/chooseForm");
    } else {
      setPage((prev: number) => prev - 1);
    }
  }

  const handleNext = () => {
    const filtered = (Object.keys(data) as IndividualFormDataProps[])
      .filter((key) => { return req[page].includes(key) })
      .reduce((obj, key) => {
        // eslint-disable-next-line @typescript-eslint/ban-ts-comment
        // @ts-expect-error
        obj[key] = data[key];
        return obj;
      }, {} as IndividualFormData);

    const errors = validate(filtered);

    if (Object.keys(errors).length > 0) {
      setError(errors);
    } else {
      setPage(prev => prev + 1);
    }
  }

  const validate = (val: IndividualFormData) => {
    const errors: FormErrors = {};

    (Object.entries(val) as [string, (string | boolean)][]).forEach(([key , value]) => {
      if (key === 'email') {
        if(!(/^\w+([.-]?\w+)*@\w+([.-]?\w+)*(\.\w{2,3})+$/.test(value as string))) {
          errors.email = true;
        }
      } else {
        if (value === '' || value === false) {
          errors[key as keyof FormErrors]= true;
        }
      }
    });
    return errors;
  }

  const handleSubmit = () => {

    const body: CandidateBody = {
      candidate: {
        candidateType: "individual",
        name: data.name,
        email: data.email,
        country: data.country,
        socialX: data.socialX,
        socialLinkedin: data.socialLinkedin,
        socialDiscord: data.socialDiscord,
        socialTelegram: data.socialTelegram,
        socialOther: data.socialOther,
        publicContact: data.publicContact,
        about: data.about,
        bio: data.bio,
        additionalInfo: data.additionalInfo,
        videoPresentationLink: data.videoPresentationLink,
        reasonToServe: data.reasonToServe,
        governanceExperience: data.governanceExperience,
        communicationStrategy: data.communicationStrategy,
        ecosystemContributions: data.ecosystemContributions,
        legalExpertise: data.legalExpertise,
        weeklyCommitmentHours: Number(data.weeklyCommitmentHours),
        conflictOfInterest: data.conflictOfInterest,
        drepId: data.drepId,
        stakeId: data.stakeId,
        xverification: data.xverification,
      }
    };

    mutate(body);
    navigate('/thankYou');
  }

  return (
    <Layout>
      <Box>
        <TopNav />
        <Box sx={{ display: 'flex', justifyContent: 'center', marginTop: '24px', marginBottom: '56px' }}>
          <Box className={styles.container}>
            <Typography variant="h1">{title[page]}</Typography>
            {display[page]}
            <Box sx={{ display: 'flex', justifyContent: 'space-between', paddingTop: '16px' }}>
              <Button variant="text" onClick={handleBack}>
                Back
              </Button>
              <Button
                variant="text"
                endIcon={<img src={ICONS.arrowCircleRight} alt="" />}
                onClick={isSubmit ? handleSubmit : handleNext}
                isLoading={isLoading}
              >
                {isSubmit ? 'Submit' : 'Next'}
              </Button>
            </Box>
          </Box>
        </Box>
      </Box>
    </Layout>
  )
}
