// The dashboard uses ordinary server-rendered forms; no JavaScript framework is required.
document.querySelectorAll('form[data-confirm]').forEach(form=>form.addEventListener('submit',event=>{if(!window.confirm(form.dataset.confirm))event.preventDefault()}));
