document.addEventListener('DOMContentLoaded', function () {
    const backToTopButton = document.getElementById('back-to-top');
    let isVisible = false;

    function updateProgress() {
        const scrollY = window.scrollY;
        const documentHeight = document.documentElement.scrollHeight - window.innerHeight;
        const progress = (scrollY / documentHeight) * 100;

        // Update the button's pseudo-element background to reflect the scroll progress
        backToTopButton.style.setProperty('--progress', progress);
        backToTopButton.style.setProperty('--gradient', `conic-gradient(var(--secondary-color) ${progress}%, transparent ${progress}%)`);
        backToTopButton.style.setProperty('background', `conic-gradient(var(--secondary-color) ${progress}%, transparent ${progress}%)`);
    }

    window.addEventListener('scroll', function () {
        if (window.scrollY > 300) {
            if (!isVisible) {
                backToTopButton.classList.add('show');
                backToTopButton.classList.remove('hide');
                isVisible = true;
            }
            updateProgress();
        } else {
            if (isVisible) {
                backToTopButton.classList.add('hide');
                setTimeout(() => {
                    backToTopButton.classList.remove('show');
                    isVisible = false;
                }, 400);
            }
        }
    });

    backToTopButton.addEventListener('click', function () {
        window.scrollTo({
            top: 0,
            behavior: 'smooth'
        });
    });
});
