import Layout from "../../components/Layout/Layout";

const optionsForScroll = [
  {
    label: "Section 1",
    content: (
      <>
        <h2>Scroll Mode</h2>
        <h2>Scroll Mode</h2>
      </>
    ),
  },
  {
    label: "Section 2",
    content: (
      <>
          <h2>Scroll Mode</h2>
          <h2>Scroll Mode</h2>
          <h2>Scroll Mode</h2>
          <h2>Scroll Mode</h2>
      </>
    ),
  },
  {
    label: "Section 3",
    content: (
      <>
          <h2>Scroll Mode</h2>
          <h2>Scroll Mode</h2>
          <h2>Scroll Mode</h2>
          <h2>Scroll Mode</h2>
      </>
    ),
  },
];

const optionsForChange = [
  { label: "Option 1", content: <div>Content for Option 1</div> },
  { label: "Option 2", content: <div>Content for Option 2</div> },
];

const TermsAndConditions2 = () => {
  return (
    <div>
      <h2>Scroll Mode</h2>
      <Layout menuOptions={optionsForScroll} mode="scroll" />

      <h2>Change Content Mode</h2>
      <Layout menuOptions={optionsForChange} mode="change" />
    </div>
  );
};

export default TermsAndConditions2;
