interface Category {
  id: string;
  name: string;
  presentationName: string;
  description: string;
  nominees: {
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

export type { Category };
