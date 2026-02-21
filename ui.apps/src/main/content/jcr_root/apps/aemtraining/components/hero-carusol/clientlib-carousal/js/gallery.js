document.addEventListener("DOMContentLoaded", () => {

  const track = document.getElementById("carouselTrack");
  const slides = track.children;

  const nextBtn = document.getElementById("nextBtn");
  const prevBtn = document.getElementById("prevBtn");
  const dotsContainer = document.getElementById("dots");

  let index = 0;

  // Create Dots
  for (let i = 0; i < slides.length; i++) {
    const dot = document.createElement("button");
    dot.className =
      "w-3 h-3 rounded-full bg-gray-400 hover:bg-gray-700 transition";
    dot.addEventListener("click", () => goToSlide(i));
    dotsContainer.appendChild(dot);
  }

  function updateDots() {
    [...dotsContainer.children].forEach((dot, i) => {
      dot.className =
        i === index
          ? "w-4 h-4 rounded-full bg-black transition"
          : "w-3 h-3 rounded-full bg-gray-400 hover:bg-gray-700 transition";
    });
  }

  function goToSlide(i) {
    index = i;
    track.style.transform = `translateX(-${index * 100}%)`;
    updateDots();
  }

  function nextSlide() {
    index = (index + 1) % slides.length;
    goToSlide(index);
  }

  function prevSlide() {
    index = (index - 1 + slides.length) % slides.length;
    goToSlide(index);
  }

  nextBtn.addEventListener("click", nextSlide);
  prevBtn.addEventListener("click", prevSlide);

  // AutoPlay
  setInterval(nextSlide, 5000);

  updateDots();

});
