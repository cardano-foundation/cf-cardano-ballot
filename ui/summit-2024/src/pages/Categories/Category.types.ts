interface Category {
  id: string;
  presentationName: string;
  desc: string;
  gdprProtection: boolean;
  proposals: {
    id: string;
    name: string;
    presentationName: string;
    desc: string;
    url: string;
    imageUrl: string;
    active: boolean;
  }[];
  active: boolean;
}

interface CategoryContent {
  id: string;
  presentationName: string;
  desc: string;
  proposals: {
    id: string;
    presentationName: string;
    desc: string;
  }[];
}

export type { Category, CategoryContent };
