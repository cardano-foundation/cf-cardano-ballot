interface UserGuideLink {
  label: string;
  url: string;
}

interface UserGuideSection {
  number: number;
  title: string;
  description: string;
  link?: UserGuideLink;
}

interface UserGuideMenuEntry {
  label: string;
  title: string;
  sections: UserGuideSection[];
}

const userGuideMenu: UserGuideMenuEntry[] = [
  {
    label: "What You’ll Need",
    title: "To Submit Votes, You’ll Need:",
    sections: [
      {
        number: 1,
        title: "The ability to receive an SMS verification message.",
        description:
          "Securely verify your account with a one-time SMS code for Cardano Ballot. Safety and simplicity combined.",
      },
      {
        number: 2,
        title: "A supported Cardano wallet and/or browser extension.",
        description:
          "You don't need to have any funds in your wallet to use Cardano Ballot.",
        link: {
          label: "View a list of supported wallets",
          url: "#",
        },
      },
    ],
  },
  {
    label: "Creating an Account",
    title: "Create and Verify Your Account:",
    sections: [
      {
        number: 1,
        title:
          "Click on 'Connect Wallet' and choose a supported wallet from the list.",
        description:
          "By default, only Flint (Desktop/Mobile) and installed supported wallets will be shown.",
      },
      {
        number: 2,
        title:
          "Verify your wallet using CIP8 message signing through SMS or Discord.",
        description:
          "Once you connect your wallet you will be prompted for verification, if you choose to skip this step until later you can access this again by clicking your wallet in the top right corner, or by trying to vote via the categories page. Protect your Cardano Ballot account with seamless verification using CIP8 message signing via SMS or Discord.",
      },
    ],
  },
  {
    label: "How to Vote",
    title: "How to Submit a Vote:",
    sections: [
      {
        number: 1,
        title: "Navigate to the “Categories” section.",
        description:
          "You can do this by either clicking on the link in at the top of the page labelled “Categories” or by clicking on the “Start Voting” button on the home page and directly below this section!",
      },
      {
        number: 2,
        title: "Scroll through the voting categories.",
        description:
          "On the left-hand side, you will see the proposed voting categories. Simply click on the category you wish to vote on to see the corresponding list of nominees.",
      },
      {
        number: 3,
        title: "Browse and choose your nominee.",
        description:
          "To see additional information about a nominee click on the “Learn More” button. To select the nominee you want to vote for, simply click on their listing to make the selection.",
      },
      {
        number: 4,
        title: "Click “Vote Now” to cast your vote.",
        description:
          "Once you have made your selection you will be able to click on the “Vote Now” button in the top right above the nominees listings.",
      },
      {
        number: 5,
        title: "Connect and/ or verify your wallet",
        description:
          "If you haven’t yet connected or verified your wallet you will be prompted to do so at this point. Follow the instructions from your chosen wallet.",
      },
      {
        number: 6,
        title: "Your vote is now submitted!",
        description:
          "Once you follow the signing process from your wallet your vote will be submitted. To see the progress status of your vote click the “Vote Receipt” button in the top right above the nominees listings",
      },
    ],
  },
];

export type { UserGuideMenuEntry };
export { userGuideMenu };
