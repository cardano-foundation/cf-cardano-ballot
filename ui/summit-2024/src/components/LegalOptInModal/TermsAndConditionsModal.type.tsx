interface Definition {
    [key: string]: string;
}

interface Subsection {
    title: string;
    content: string[];
    definitions?: Definition;
}

interface Section {
    title: string;
    content?: string[];
    subsections?: Subsection[];
}

interface List {
    number: string;
    content: string[];
}

interface TermSection {
    title: string;
    list: List[];
}

interface TermsData {
    title: string;
    date: string;
    sections: Section[];
    terms: TermSection[];
    disclaimer?: {
        title: string;
        content: string[];
    };
    liability?: {
        title: string;
        content: string[];
    };
    miscellaneous?: {
        title: string;
        list: List[];
    };
    contactus?: {
        title: string;
        content: string;
    };
}

export {
    Definition,
    Subsection,
    Section,
    List,
    TermSection,
    TermsData
}