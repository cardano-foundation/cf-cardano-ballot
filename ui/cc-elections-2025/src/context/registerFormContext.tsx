import { createContext, ReactNode, useEffect, useState } from "react";
import { ErrObject, FormContextType, RegisterFormData } from "@/types/formData.ts";
import { MemberBody } from "@models";
import { SelectChangeEvent } from "@mui/material";

export const RegisterFormContext = createContext<FormContextType<RegisterFormData> | null>(null);

type FormProviderProps = {
  children: ReactNode;
}

export const RegisterFormProvider = ({ children }: FormProviderProps) => {

  const title = [
    'Constitutional Committee application',
    'Optional Tasks to Demonstrate Capabilities',
    'What type of candidate are you?',
    'Members Informations',
    'Informations',
    'Additional candidate information',
    'Verifications',
  ]

  const memberInit: MemberBody = {
    name: '',
    country: '',
    bio: '',
    socialX: '',
    socialLinkedin: '',
    socialDiscord: '',
    socialTelegram: '',
    socialWebsite: '',
    socialOther: '',
    liveliness: '',
    conflictOfInterest: '',
    drepId: '',
    stakeId: '',
    xverification: '',
  };

  const req = [
    ['guidelines', 'privacyPolicy', 'termsOfUse'],
    [],
    [],
    [],
    [
      'name',
      'email',
      'bio',
      'about',
      'videoPresentationLink',
      'socialLinkedin',
      'socialX',
      'socialDiscord',
      'socialTelegram',
      'socialOther',
      'socialWebsite',
      'publicContact',
    ],
    [],
    [],
  ];

  const [page, setPage] = useState<number>(0);

  const [candidateType, setCandidateType] = useState<"individual" | "company" | "consortium" | null>(null);

  const [error, setError] = useState<ErrObject>({});

  const [data, setData] = useState<RegisterFormData>({
    guidelines: false,
    privacyPolicy: false,
    termsOfUse: false,
    coldCredential: '',
    governanceActionRationale: '',
    name: '',
    registrationNumber: '',
    keyContactPerson: '',
    socialWebsite: '',
    email: '',
    country: '',
    socialX: '',
    socialLinkedin: '',
    socialDiscord: '',
    socialTelegram: '',
    socialOther: '',
    publicContact: '',
    about: '',
    bio: '',
    additionalInfo: '',
    videoPresentationLink: '',
    reasonToServe: '',
    governanceExperience: '',
    communicationStrategy: '',
    ecosystemContributions: '',
    legalExpertise: '',
    weeklyCommitmentHours: '',
    liveliness: '',
    conflictOfInterest: '',
    drepId: '',
    stakeId: '',
    xverification: '',
    members: [],
    membersAmount: 0,
  });

  useEffect(() => {
    if (candidateType === 'consortium') {
      setData(prevData => ({...prevData, members: Array.from({ length: 2 }, () => memberInit), membersAmount: 2}));
    } else {
      setData(prevData => ({...prevData, members: [], membersAmount: 0}));
    }
  }, [candidateType]);

  const isEventInputElement = (event: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>  | SelectChangeEvent<unknown>): event is React.ChangeEvent<HTMLInputElement> => {
    return (event as React.ChangeEvent<HTMLInputElement>).target.checked !== undefined;
  }

  const handleChange = (
    event: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement> | SelectChangeEvent<unknown>,
  ) => {
    if (event.target) {
      const type = isEventInputElement(event) ? event.target.type : 'select';

      const name = event.target.name;
      const value = type === 'checkbox' && isEventInputElement(event) ? event.target.checked : event.target.value;

      setData(prevData => ({...prevData, [name]: value}));
    }
  }

  const handleMemberChange = (
    event: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement> | SelectChangeEvent<unknown>,
    index: number,
  ) => {
    if (event.target) {
      const type = isEventInputElement(event) ? event.target.type : 'select';

      const name = event.target.name;
      const value = type === 'checkbox' && isEventInputElement(event) ? event.target.checked : event.target.value;

      setData(prevData => ({...prevData, members: prevData.members.map((dataItem, itemIndex) => {
          if(index === itemIndex) {
            return { ...dataItem, [name]: value }
          } else {
            return { ...dataItem };
          }
        })}));
    }
  }

  return (
    <RegisterFormContext.Provider
      value={{
        candidateType,
        data,
        error,
        handleChange,
        handleMemberChange,
        memberInit,
        page,
        req,
        setCandidateType,
        setData,
        setError,
        setPage,
        title,
      }}
    >
      {children}
    </RegisterFormContext.Provider>
  )
}
