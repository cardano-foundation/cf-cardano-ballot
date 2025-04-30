import { useContext } from "react";
import { useNavigate } from "react-router-dom";
import Box from "@mui/material/Box";
import Typography from "@mui/material/Typography";
import {Layout} from "./Layout/Layout";
import { TopNav } from "./TopNav";

import { FormStep1 } from "./FormSteps/FormStep1.tsx";
import { FormStep2 } from "./FormSteps/FormStep2.tsx";
import { FormStep3 } from "./FormSteps/FormStep3.tsx";
import { FormStep4 } from "./FormSteps/FormStep4.tsx";
import { FormStep5 } from "./FormSteps/FormStep5.tsx";
import { FormStep6 } from "./FormSteps/FormStep6.tsx";
import { FormStep7 } from "./FormSteps/FormStep7.tsx";

import { Button } from "@atoms";
import { ICONS } from "@consts";
import { RegisterFormContext, useCardano } from "@context";
import { usePostCandidate } from "@hooks";
import { CandidateBody } from "@models";
import { CCStepper } from "@/components/molecules/CCStepper.tsx";

import type { RegisterFormData, FormContextType } from '../types/formData';

import styles from "./molecules/FormCard.module.scss";

type RegisterFormDataProps = keyof RegisterFormData;

type FormErrors = {
  termsAndCondition?: boolean;
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
    setError,
    candidateType,
  } = useContext(RegisterFormContext) as FormContextType<RegisterFormData>;

  const informationTitle = candidateType && (candidateType === 'individual' ?
    'Candidate' :
    candidateType?.charAt(0).toUpperCase() + candidateType?.slice(1));

  const steps = [`${informationTitle} Informations`, 'Members informations', 'Additional Informations', 'Verifications'];

  const { address } = useCardano();

  const postCandidate = usePostCandidate(!candidateType ? 'individual' : candidateType);

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

  const isSubmit = page === display.length - 1;

  const handleDiscard = () => {
    navigate("/");
  }

  const handleBack = () => {
    if(page === 0) {
      navigate("/");
    } else {
      setPage(prev => {
        if (page === 5 && candidateType !== 'consortium') {
          return prev - 2;
        }
        return prev - 1
      });
    }
  }

  const handleNext = () => {
    const filtered = (Object.keys(data) as RegisterFormDataProps[])
      .filter((key) => { return req[page].includes(key) })
      .reduce((obj, key) => {
        // eslint-disable-next-line @typescript-eslint/ban-ts-comment
        // @ts-expect-error
        obj[key] = data[key];
        return obj;
      }, {} as RegisterFormData);

    const errors = validate(filtered);

    if (Object.keys(errors).length > 0) {
      setError(errors);
    } else {
      if (candidateType === null && page === 1) {
        setError(errors => ({
          ...errors,
          candidateType: true,
        }));
      } else {
        setError(() => ({}));
        setPage(prev => {
          if (page === 3 && candidateType !== 'consortium') {
            return prev + 2;
          }
          return prev + 1
        });
      }
    }
  }

  const validate = (val: RegisterFormData) => {
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
    if (candidateType === null) {
      return;
    }

    const body: CandidateBody = {
      candidate: {
        candidateType: candidateType,
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
        walletAddress: address ? address : '',
        xverification: data.xverification,
      },
      registrationNumber: candidateType === 'company' ? data.registrationNumber : undefined,
      keyContactPerson: candidateType === 'company' ? data.keyContactPerson : undefined,
      socialWebsite: candidateType === 'company' ? "" : undefined,
      members: candidateType === 'consortium' ? data.members : undefined,
    };

    postCandidate.mutate(body);
    navigate('/thankYou');
  }

  return (
    <Layout>
      <Box>
        <TopNav title="Apply as a candidate" navigateBack={false} />
        {page > 2 && (
          <Box sx={{ maxWidth: '1440px', padding: '24px 0'}}>
            <CCStepper
              activeStep={candidateType !== 'consortium' && page > 3 ? page - 4 : page - 3}
              steps={candidateType !== 'consortium' ? steps.filter(v => !v.includes('Members')) : steps}
            />
          </Box>
        )}
        <Box sx={{ display: 'flex', justifyContent: 'center', marginTop: '24px', marginBottom: '56px' }}>
          <Box className={styles.container}>
            <Typography variant="h1">
              {
                page === 3 ?
                  `${informationTitle} ${title[page]}`
                  : title[page]
              }
            </Typography>
            {display[page]}
            <Box sx={{ display: 'flex', justifyContent: 'space-between', paddingTop: '16px' }}>
              <Button variant="text" onClick={handleDiscard}>
                Discard
              </Button>
              <Box sx={{ display: 'flex', gap: '12px' }}>
                {page > 1 && (
                  <Button
                    variant="text"
                    onClick={handleBack}
                    startIcon={<img src={ICONS.arrowCircleLeft} alt="" />}
                  >
                    Back
                  </Button>
                )}
                <Button
                  variant="text"
                  endIcon={<img src={ICONS.arrowCircleRight} alt="" />}
                  onClick={isSubmit ? handleSubmit : handleNext}
                  isLoading={postCandidate && postCandidate.isLoading}
                >
                  {isSubmit ? 'Submit' : 'Next'}
                </Button>
              </Box>
            </Box>
          </Box>
        </Box>
      </Box>
    </Layout>
  )
}
