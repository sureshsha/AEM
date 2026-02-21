document.addEventListener("DOMContentLoaded", () => {

  const items = document.querySelectorAll(".group[data-src]");
  const lightbox = document.getElementById("lightbox");

  const lightboxImg = document.getElementById("lightboxImg");
  const caption = document.getElementById("lightboxCaption");

  const closeBtn = document.getElementById("closeBtn");
  const nextBtn = document.getElementById("nextBtn");
  const prevBtn = document.getElementById("prevBtn");

  let currentIndex = 0;

  function openLightbox(index) {
    currentIndex = index;
    updateLightbox();
    lightbox.classList.remove("hidden");
  }

  function closeLightbox() {
    lightbox.classList.add("hidden");
  }

  function updateLightbox() {
    const src = items[currentIndex].dataset.src;
    const text = items[currentIndex].dataset.caption;

    lightboxImg.src = src;
    caption.innerText = text;
  }

  function nextImage() {
    currentIndex = (currentIndex + 1) % items.length;
    updateLightbox();
  }

  function prevImage() {
    currentIndex = (currentIndex - 1 + items.length) % items.length;
    updateLightbox();
  }

  // Click image
  items.forEach((item, index) => {
    item.addEventListener("click", () => openLightbox(index));
  });

  // Buttons
  closeBtn.addEventListener("click", closeLightbox);
  nextBtn.addEventListener("click", nextImage);
  prevBtn.addEventListener("click", prevImage);

  // ESC key close
  document.addEventListener("keydown", (e) => {
    if (e.key === "Escape") closeLightbox();
  });

});
