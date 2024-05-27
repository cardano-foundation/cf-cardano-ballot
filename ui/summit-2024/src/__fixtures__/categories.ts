interface NomineeFixture {
  id: number;
  name: string;
  description: string;
  twitterLink: string;
  linkedinLink: string;
  website: string;
}

interface NomineeArrayFixture {
  category: string;
  nominees: NomineeFixture[];
}

const nomineesData: NomineeArrayFixture[] = [
  {
    category: "Ambassador",
    nominees: [
      {
        id: 1,
        name: "John Doe",
        description:
          "John has been instrumental in advocating for blockchain technology across global platforms.",
        twitterLink: "https://twitter.com/johndoe",
        linkedinLink: "https://linkedin.com/in/johndoe",
        website: "https://johndoe.com",
      },
      {
        id: 2,
        name: "Jane Smith",
        description:
          "Jane has led numerous initiatives to promote cryptocurrency adoption.",
        twitterLink: "https://twitter.com/janesmith",
        linkedinLink: "https://linkedin.com/in/janesmith",
        website: "https://janesmith.com",
      },
      {
        id: 3,
        name: "John Loo",
        description:
          "John has been instrumental in advocating for blockchain technology across global platforms.",
        twitterLink: "https://twitter.com/johndoe",
        linkedinLink: "https://linkedin.com/in/johndoe",
        website: "https://johndoe.com",
      },
      {
        id: 4,
        name: "Jane Peer",
        description:
          "Jane has led numerous initiatives to promote cryptocurrency adoption.",
        twitterLink: "https://twitter.com/janesmith",
        linkedinLink: "https://linkedin.com/in/janesmith",
        website: "https://janesmith.com",
      },
      {
        id: 5,
        name: "John See",
        description:
          "John has been instrumental in advocating for blockchain technology across global platforms.",
        twitterLink: "https://twitter.com/johndoe",
        linkedinLink: "https://linkedin.com/in/johndoe",
        website: "https://johndoe.com",
      },
      {
        id: 6,
        name: "Jane Laa",
        description:
          "Jane has led numerous initiatives to promote cryptocurrency adoption.",
        twitterLink: "https://twitter.com/janesmith",
        linkedinLink: "https://linkedin.com/in/janesmith",
        website: "https://janesmith.com",
      },
      {
        id: 7,
        name: "John Xee",
        description:
          "John has been instrumental in advocating for blockchain technology across global platforms.",
        twitterLink: "https://twitter.com/johndoe",
        linkedinLink: "https://linkedin.com/in/johndoe",
        website: "https://johndoe.com",
      },
      {
        id: 8,
        name: "Jane Clas",
        description:
          "Jane has led numerous initiatives to promote cryptocurrency adoption.",
        twitterLink: "https://twitter.com/janesmith",
        linkedinLink: "https://linkedin.com/in/janesmith",
        website: "https://janesmith.com",
      },
      {
        id: 9,
        name: "Jane Asu",
        description:
          "Jane has led numerous initiatives to promote cryptocurrency adoption.",
        twitterLink: "https://twitter.com/janesmith",
        linkedinLink: "https://linkedin.com/in/janesmith",
        website: "https://janesmith.com",
      },
      {
        id: 10,
        name: "Jane Wu",
        description:
          "Jane has led numerous initiatives to promote cryptocurrency adoption.",
        twitterLink: "https://twitter.com/janesmith",
        linkedinLink: "https://linkedin.com/in/janesmith",
        website: "https://janesmith.com",
      },
      // Add more nominees as needed
    ],
  },
  {
    category: "Blockchain for Good",
    nominees: [
      {
        id: 1,
        name: "Alice Johnson",
        description:
          "Alice has developed several blockchain solutions that address social issues.",
        twitterLink: "https://twitter.com/alicejohnson",
        linkedinLink: "https://linkedin.com/in/alicejohnson",
        website: "https://alicejohnson.com",
      },
      {
        id: 2,
        name: "Bob Brown",
        description:
          "Bob's work in blockchain for humanitarian aid has been recognized globally.",
        twitterLink: "https://twitter.com/bobbrown",
        linkedinLink: "https://linkedin.com/in/bobbrown",
        website: "https://bobbrown.com",
      },
      // Add more nominees as needed
    ],
  },
  {
    category: "DeFi / DEX Platform",
    nominees: [
      {
        id: 1,
        name: "Carol King",
        description:
          "Carol has been a pioneer in developing decentralized finance platforms that are secure and efficient.",
        twitterLink: "https://twitter.com/carolking",
        linkedinLink: "https://linkedin.com/in/carolking",
        website: "https://carolking.com",
      },
      {
        id: 2,
        name: "Dave Knight",
        description:
          "Dave has contributed to the creation of several high-profile decentralized exchanges.",
        twitterLink: "https://twitter.com/daveknight",
        linkedinLink: "https://linkedin.com/in/daveknight",
        website: "https://daveknight.com",
      },
      // Add more nominees as needed
    ],
  },
  {
    category: "Developer or Developer Tools",
    nominees: [
      {
        id: 1,
        name: "Eva Green",
        description:
          "Eva has created multiple tools that have simplified the lives of blockchain developers.",
        twitterLink: "https://twitter.com/evagreen",
        linkedinLink: "https://linkedin.com/in/evagreen",
        website: "https://evagreen.com",
      },
      {
        id: 2,
        name: "Frank Gale",
        description:
          "Frank's innovative developer tools have accelerated the adoption of blockchain technologies.",
        twitterLink: "https://twitter.com/frankgale",
        linkedinLink: "https://linkedin.com/in/frankgale",
        website: "https://frankgale.com",
      },
      // Add more nominees as needed
    ],
  },
  {
    category: "Educational Influencer",
    nominees: [
      {
        id: 1,
        name: "Grace Hopper",
        description:
          "Grace is known for her educational content that demystifies complex blockchain concepts.",
        twitterLink: "https://twitter.com/gracehopper",
        linkedinLink: "https://linkedin.com/in/gracehopper",
        website: "https://gracehopper.com",
      },
      {
        id: 2,
        name: "Harry Potter",
        description:
          "Harry has influenced many through his workshops and seminars on cryptocurrency basics.",
        twitterLink: "https://twitter.com/harrypotter",
        linkedinLink: "https://linkedin.com/in/harrypotter",
        website: "https://harrypotter.com",
      },
      // Add more nominees as needed
    ],
  },
  {
    category: "Marketplace",
    nominees: [
      {
        id: 1,
        name: "Irene Adler",
        description:
          "Irene has established a leading marketplace that leverages blockchain for secure transactions.",
        twitterLink: "https://twitter.com/ireneadler",
        linkedinLink: "https://linkedin.com/in/ireneadler",
        website: "https://ireneadler.com",
      },
      {
        id: 2,
        name: "James Bond",
        description:
          "James's platform has revolutionized the way digital assets are traded online.",
        twitterLink: "https://twitter.com/jamesbond",
        linkedinLink: "https://linkedin.com/in/jamesbond",
        website: "https://jamesbond.com",
      },
      // Add more nominees as needed
    ],
  },
  {
    category: "Most Impactful SSPO",
    nominees: [
      {
        id: 1,
        name: "Karen White",
        description:
          "Karen's stake pool operations are noted for their efficiency and high uptime.",
        twitterLink: "https://twitter.com/karenwhite",
        linkedinLink: "https://linkedin.com/in/karenwhite",
        website: "https://karenwhite.com",
      },
      {
        id: 2,
        name: "Liam Neeson",
        description:
          "Liam has been influential in promoting sustainable practices in stake pool operations.",
        twitterLink: "https://twitter.com/liamneeson",
        linkedinLink: "https://linkedin.com/in/liamneeson",
        website: "https://liamneeson.com",
      },
      // Add more nominees as needed
    ],
  },
  {
    category: "NFT Project",
    nominees: [
      {
        id: 1,
        name: "Molly Hooper",
        description:
          "Molly's NFT project focuses on digital art and has gained significant attention in the art world.",
        twitterLink: "https://twitter.com/mollyhooper",
        linkedinLink: "https://linkedin.com/in/mollyhooper",
        website: "https://mollyhooper.com",
      },
      {
        id: 2,
        name: "Nathan Drake",
        description:
          "Nathan has developed an NFT platform that supports emerging artists from developing countries.",
        twitterLink: "https://twitter.com/nathandrake",
        linkedinLink: "https://linkedin.com/in/nathandrake",
        website: "https://nathandrake.com",
      },
      // Add more nominees as needed
    ],
  },
  {
    category: "Standards (CIPs)",
    nominees: [
      {
        id: 1,
        name: "Olivia Dunham",
        description:
          "Olivia has been a key figure in the development and implementation of new blockchain standards.",
        twitterLink: "https://twitter.com/oliviadunham",
        linkedinLink: "https://linkedin.com/in/oliviadunham",
        website: "https://oliviadunham.com",
      },
      {
        id: 2,
        name: "Peter Bishop",
        description:
          "Peter's contributions to blockchain interoperability standards have been pivotal.",
        twitterLink: "https://twitter.com/peterbishop",
        linkedinLink: "https://linkedin.com/in/peterbishop",
        website: "https://peterbishop.com",
      },
      // Add more nominees as needed
    ],
  }
];

export { CategoriesNames, nomineesData };
export type { NomineeFixture, NomineeArrayFixture };
