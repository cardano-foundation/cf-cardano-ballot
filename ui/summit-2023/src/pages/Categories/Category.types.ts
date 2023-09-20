interface Category {
  id: string;
  name: string;
  presentationName: string;
  description: string;
  proposals: {
    id: string;
    name: string;
    presentationName: string;
    description: string;
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
