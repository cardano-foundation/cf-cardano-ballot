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
  members?: { name?: boolean, bio?: boolean, socialLinkedin?: boolean, socialX?: boolean, socialDiscord?: boolean, socialTelegram?: boolean, socialOther?: boolean, socialWebsite?: boolean }[];
  termsOfUse?: boolean;
  guidelines?: boolean;
  privacyPolicy?: boolean;
  name?: boolean;
  email?: boolean;
  about?: boolean;
  bio?: boolean;
  videoPresentationLink? : boolean;
  publicContact?: boolean;
  socialLinkedin?: boolean;
  socialX?: boolean;
  socialDiscord?: boolean;
  socialTelegram?: boolean;
  socialOther?: boolean;
  socialWebsite?: boolean;
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

  const steps = ['Members informations', `${informationTitle} Informations`, 'Additional Informations', 'Verifications'];

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

  const scrollToTop = () => {
    window.scrollTo(0, 0);
  };

  const handleBack = () => {
    if(page === 0) {
      navigate("/");
    } else {
      setPage(prev => {
        if (page === 4 && candidateType !== 'consortium') {
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

    if (page === 3) {
      const membersErrors: FormErrors['members'] = [];
      data.members.forEach((member, index) => {
        if (member.name === '')
          membersErrors[index] = { name: true };
        if (member.bio === '')
          membersErrors[index] = {...membersErrors[index], bio: true};
        if (!(/(^https?:\/\/(www.|[a-z]{2}.)?linkedin.com\/(mwlite\/|m\/)?in\/([A-Za-z0-9-_%]+)\/?$)/.test(member.socialLinkedin)) && member.socialLinkedin !== '')
          membersErrors[index] = {...membersErrors[index], socialLinkedin: true};
        if (!(/(^https?:\/\/x.com\/([A-Za-z0-9_]{1,15})\/?$)/.test(member.socialX)) && member.socialX !== '')
          membersErrors[index] = {...membersErrors[index], socialX: true};
        if (!(/(^https?:\/\/discord(app)?.com\/users\/\d{17,20}\/?$)/.test(member.socialDiscord)) && member.socialDiscord !== '')
          membersErrors[index] = {...membersErrors[index], socialDiscord: true};
        if (!(/(^https?:\/\/(t.me|telegram.me)\/[a-zA-Z0-9_]{5,32}\/?$)/.test(member.socialTelegram)) && member.socialTelegram !== '')
          membersErrors[index] = {...membersErrors[index], socialTelegram: true};
        if (!(/^(https?:\/\/)?([\w\-]+\.)+[\w\-]+(\/[\w\-._~:\/?#[\]@!$&'()*+,;=]*)?$/.test(member.socialOther)) && member.socialOther !== '')
          membersErrors[index] = {...membersErrors[index], socialOther: true};
        if (!(/^(https?:\/\/)?([\w\-]+\.)+[\w\-]+(\/[\w\-._~:\/?#[\]@!$&'()*+,;=]*)?$/.test(member.socialWebsite)) && member.socialWebsite !== '')
          membersErrors[index] = {...membersErrors[index], socialWebsite: true};
      });

      if (membersErrors.length > 0) {
        errors.members = membersErrors;
      }
    }

    if (Object.keys(errors).length > 0) {
      setError(errors);
    } else {
      if (candidateType === null && page === 2) {
        setError(errors => ({
          ...errors,
          candidateType: true,
        }));
      } else {
        setError(() => ({}));
        setPage(prev => {
          if (page === 2 && candidateType !== 'consortium') {
            return prev + 2;
          }
          return prev + 1
        });
      }
      scrollToTop();
    }
  }

  const validate = (val: RegisterFormData) => {
    const errors: FormErrors = {};

    (Object.entries(val) as [string, (string | boolean)][]).forEach(([key , value]) => {
      if (key === 'email') {
        if (!(/^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/.test(value as string))) {
          errors.email = true;
        }
      } else if(key === 'videoPresentationLink') {
        if (!(/(?:http?s?:\/\/)?(?:www.)?(?:m.)?(?:music.)?youtu(?:\.?be)(?:\.com)?(?:(?:\w*.?:\/\/)?\w*.?\w*-?.?\w*\/(?:embed|e|v|watch|.*\/)?\??(?:feature=\w*\.?\w*)?&?(?:v=)?\/?)([\w\d_-]{11})(?:\S+)?/.test(value as string)) && value !== '') {
          errors.videoPresentationLink = true;
        }
      } else if(key === 'socialLinkedin') {
        if (!(/(^https?:\/\/(www.|[a-z]{2}.)?linkedin.com\/(mwlite\/|m\/)?in\/([A-Za-z0-9-_%]+)\/?$)/.test(value as string)) && value !== '') {
          errors.socialLinkedin = true;
        }
      } else if(key === 'socialX') {
        if (!(/(^https?:\/\/x.com\/([A-Za-z0-9_]{1,15})\/?$)/.test(value as string)) && value !== '') {
          errors.socialX = true;
        }
      } else if(key === 'socialDiscord') {
        if (!(/(^https?:\/\/discord(app)?.com\/users\/\d{17,20}\/?$)/.test(value as string)) && value !== '') {
          errors.socialDiscord = true;
        }
      } else if(key === 'socialTelegram') {
        if (!(/(^https?:\/\/(t.me|telegram.me)\/[a-zA-Z0-9_]{5,32}\/?$)/.test(value as string)) && value !== '') {
          errors.socialTelegram = true;
        }
      } else if(key === 'socialOther') {
        if (!(/^(https?:\/\/)?([\w\-]+\.)+[\w\-]+(\/[\w\-._~:\/?#[\]@!$&'()*+,;=]*)?$/.test(value as string)) && value !== '') {
          errors.socialOther = true;
        }
      } else if(key === 'socialWebsite') {
        if (!(/^(https?:\/\/)?([\w\-]+\.)+[\w\-]+(\/[\w\-._~:\/?#[\]@!$&'()*+,;=]*)?$/.test(value as string)) && value !== '') {
          errors.socialWebsite = true;
        }
      } else if (key === 'publicContact') {
        if (!(/^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/.test(value as string)) && value !== '') {
          errors.publicContact = true;
        }
      } else {
        if (value === '' || value === false) {
          errors[key as keyof Omit<FormErrors, 'members'>]= true;
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
        coldCredential: data.coldCredential,
        governanceActionRationale: data.governanceActionRationale,
        socialX: data.socialX,
        socialLinkedin: data.socialLinkedin,
        socialDiscord: data.socialDiscord,
        socialTelegram: data.socialTelegram,
        socialWebsite: data.socialWebsite,
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
        weeklyCommitmentHours: data.weeklyCommitmentHours,
        conflictOfInterest: data.conflictOfInterest,
        drepId: data.drepId,
        stakeId: data.stakeId,
        walletAddress: address ? address : '',
        xverification: data.xverification,
      },
      registrationNumber: candidateType === 'company' ? data.registrationNumber : undefined,
      keyContactPerson: candidateType === 'company' ? data.keyContactPerson : undefined,
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
                page === 4 ?
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
