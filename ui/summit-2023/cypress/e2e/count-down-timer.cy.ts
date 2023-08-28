describe('count down timer spec', () => {
  it('passes', () => {
    cy.visit('http://localhost:3000/')
  })
  it('should timer exists', () => {
    cy.visit('http://localhost:3000/');
    cy.get('[data-testid="count-down-timer"]').contains("days");
  });
})