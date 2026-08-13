const header = document.querySelector('[data-header]');
const nav = document.querySelector('[data-nav]');
const navToggle = document.querySelector('[data-nav-toggle]');

const updateHeader = () => header?.classList.toggle('scrolled', window.scrollY > 18);
updateHeader();
window.addEventListener('scroll', updateHeader, { passive: true });

navToggle?.addEventListener('click', () => {
  const open = nav.classList.toggle('open');
  navToggle.setAttribute('aria-expanded', String(open));
  navToggle.setAttribute('aria-label', open ? 'Close navigation' : 'Open navigation');
});
nav?.querySelectorAll('a').forEach((link) => link.addEventListener('click', () => {
  nav.classList.remove('open');
  navToggle?.setAttribute('aria-expanded', 'false');
}));

document.querySelectorAll('[data-copy]').forEach((button) => {
  button.addEventListener('click', async () => {
    try {
      await navigator.clipboard.writeText(button.dataset.copy);
      const label = button.querySelector('[data-copy-label]');
      const previous = label.textContent;
      label.textContent = 'Copied';
      button.classList.add('copied');
      window.setTimeout(() => { label.textContent = previous; button.classList.remove('copied'); }, 1600);
    } catch { button.querySelector('[data-copy-label]').textContent = 'Select text'; }
  });
});

const revealObserver = new IntersectionObserver((entries) => {
  entries.forEach((entry) => { if (entry.isIntersecting) { entry.target.classList.add('visible'); revealObserver.unobserve(entry.target); } });
}, { threshold: 0.12 });
document.querySelectorAll('.reveal').forEach((element) => revealObserver.observe(element));

const countries = {
  AU: { label: 'Valid Australian number', phone: '+61 412 345 678' },
  CA: { label: 'Valid Canadian number', phone: '+1 416 555 0123' },
  NZ: { label: 'Valid New Zealand number', phone: '+64 21 123 4567' },
  US: { label: 'Valid United States number', phone: '+1 202 555 0147' }
};
const selectCountry = (code) => {
  document.querySelectorAll('[data-country], [data-select-country]').forEach((item) => item.classList.toggle('selected', item.dataset.country === code || item.dataset.selectCountry === code));
  document.querySelector('[data-validation-label]').textContent = countries[code].label;
  document.querySelector('[data-phone-format]').textContent = countries[code].phone;
};
document.querySelectorAll('[data-country], [data-select-country]').forEach((item) => item.addEventListener('click', () => selectCountry(item.dataset.country || item.dataset.selectCountry)));

const search = document.querySelector('[data-country-search]');
const rows = [...document.querySelectorAll('[data-country]')];
search?.addEventListener('input', () => {
  const query = search.value.trim().toLowerCase().replace(/^\+/, '');
  let matches = 0;
  rows.forEach((row) => { const visible = row.dataset.name.includes(query); row.hidden = !visible; matches += Number(visible); });
  document.querySelector('[data-empty]').hidden = matches !== 0;
});
