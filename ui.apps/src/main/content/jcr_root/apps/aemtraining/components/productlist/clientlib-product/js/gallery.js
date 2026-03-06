document.addEventListener("DOMContentLoaded", () => {

    document.querySelectorAll(".productlist-container").forEach(container => {

        const resourcePath = container.getAttribute("data-resource");

        fetch(resourcePath + ".json")
            .then(res => res.json())
            .then(products => {

                // Tailwind grid layout
                container.innerHTML = `
                    <div class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6 p-4">
                        ${products.map(product => `
                            <div class="border p-4 rounded-lg shadow hover:shadow-lg transition bg-white">
                                <img src="${product.image}"
                                     class="h-40 mx-auto object-contain mb-4" />

                                <h3 class="font-semibold text-lg mb-2">${product.title}</h3>

                                <p class="text-sm text-gray-600 h-16 overflow-hidden mb-3">
                                    ${product.description}
                                </p>

                                <div class="text-green-600 font-bold text-xl mb-2">
                                    $${product.price}
                                </div>

                                <div class="text-sm bg-gray-200 inline-block px-2 py-1 rounded">
                                    ${product.category}
                                </div>
                            </div>
                        `).join("")}
                    </div>
                `;
            })
            .catch(err => {
                container.innerHTML = `<p class="text-red-600">Failed to load products.</p>`;
            });
    });

});