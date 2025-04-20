import Box from "@mui/material/Box";
import { useNavigate } from "react-router-dom";
import { TopNav } from "./TopNav.tsx";
import Typography from "@mui/material/Typography";
import { Layout } from "./Layout/Layout.tsx";
import styles from "./molecules/FormCard.module.scss";
import { Button } from "@atoms";
import { ICONS } from "@consts";

import { FormStep1 } from "./ConsortiumFormSteps/FormStep1.tsx";
import { FormStep2 } from "./ConsortiumFormSteps/FormStep2.tsx";
import { FormStep3 } from "./ConsortiumFormSteps/FormStep3.tsx";
import { FormStep4 } from "./ConsortiumFormSteps/FormStep4.tsx";
import { FormStep5 } from "./ConsortiumFormSteps/FormStep5.tsx";
import { FormStep6 } from "./ConsortiumFormSteps/FormStep6.tsx";
import { FormStep7 } from "./ConsortiumFormSteps/FormStep7.tsx";
import { FormStep8 } from "./ConsortiumFormSteps/FormStep8.tsx";

import {IndividualFormData, FormContextType, ConsortumFormData} from '../types/formData';
import {useContext} from "react";
import { ConsortiumFormContext } from "@context";
import { usePostConsortium } from "@hooks";
import {CandidateBody} from "@models";

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
  } = useContext(ConsortiumFormContext) as FormContextType<ConsortumFormData>;

  const { mutate, isLoading } = usePostConsortium();

  const display = [
    <FormStep1 />,
    <FormStep2 />,
    <FormStep3 />,
    <FormStep4 />,
    <FormStep5 />,
    <FormStep6 />,
    <FormStep7 />,
    <FormStep8 />,
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
        candidateType: "consortium",
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
        weeklyCommitmentHours: 0,
        conflictOfInterest: '',
        drepId: '',
        stakeId: '',
        xverification: '',
      },
      members: data.members,
    };
    mutate(body);
    console.log(body);
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
