const imageInput = document.getElementById('imageInput');
    const imagePreview = document.getElementById('imagePreview');
    const newPreviewContainer = document.getElementById('new-preview-container');
    const existingPreview = document.getElementById('existing-preview');

    if (imageInput) {
        imageInput.addEventListener('change', function(e) {
            const file = e.target.files[0];
            
            if (file) {
                const reader = new FileReader();
                
                reader.onload = function(e) {
                    imagePreview.src = e.target.result;
                    newPreviewContainer.style.display = 'block';
                    if (existingPreview) existingPreview.style.display = 'none';
                }
                
                reader.readAsDataURL(file);
            } else {
                newPreviewContainer.style.display = 'none';
                if (existingPreview) existingPreview.style.display = 'block';
            }
        });
    }

	document.addEventListener('DOMContentLoaded', function() {
        const btn = document.getElementById('notificationBtn');
        const modal = document.getElementById('notificationModal');

        if (btn && modal) {
            btn.addEventListener('click', function(e) {
                e.stopPropagation();
                const isDisplayed = modal.style.display === 'block';
                modal.style.display = isDisplayed ? 'none' : 'block';
            });

            document.addEventListener('click', function(e) {
                if (!modal.contains(e.target) && e.target !== btn) {
                    modal.style.display = 'none';
                }
            });
        }
    });

// For item_form.html - Category selection
document.addEventListener('DOMContentLoaded', async function () {
    const parentSelect = document.getElementById('parentCategory');
    const form = parentSelect ? parentSelect.closest('form') : null;

    // Exit if not on item_form or item_list
    if (!form) return;

    // Logic for item_form.html
    if (form.getAttribute('enctype') === 'multipart/form-data') {
        const childSelect = document.getElementById('childCategory');
        const grandChildSelect = document.getElementById('grandChildCategory');
        const finalInput = document.getElementById('finalCategoryId');

        async function getData(url) {
            try {
                const res = await fetch(url);
                return res.ok ? await res.json() : null;
            } catch (e) { return null; }
        }

        function buildOptions(select, data, selectedId, arrowId) {
            const arrow = document.getElementById(arrowId);
            select.innerHTML = '<option value="">選択してください</option>';
            if (!data || data.length === 0) {
                select.style.display = 'none';
                if(arrow) arrow.style.display = 'none';
                return;
            }
            data.forEach(c => {
                const opt = document.createElement('option');
                opt.value = c.id;
                opt.text = c.name;
                if (c.id == selectedId) opt.selected = true;
                select.appendChild(opt);
            });
            select.style.display = 'inline-block';
            if(arrow) arrow.style.display = 'inline-block';
        }

        parentSelect.addEventListener('change', async (e) => {
            const id = e.target.value;
            finalInput.value = id;
            const children = await getData(`/items/categories/${id}/children`);
            buildOptions(childSelect, children, null, 'childArrow');
            grandChildSelect.style.display = 'none';
            document.getElementById('grandChildArrow').style.display = 'none';
        });

        childSelect.addEventListener('change', async (e) => {
            const id = e.target.value;
            finalInput.value = id || parentSelect.value;
            const grandChildren = await getData(`/items/categories/${id}/children`);
            buildOptions(grandChildSelect, grandChildren, null, 'grandChildArrow');
        });

        grandChildSelect.addEventListener('change', (e) => {
            finalInput.value = e.target.value || childSelect.value;
        });

        const existingId = finalInput.value;
        if (existingId) {
            // Restoration logic might be needed here too
        }
    }
    // Logic for item_list.html
    else if (form.classList.contains('search-form')) {
        const childSelect = document.getElementById('childCategory');
        const grandChildSelect = document.getElementById('grandChildCategory');
        const finalInput = document.getElementById('finalCategoryId');

        async function getData(url) {
            try {
                const res = await fetch(url);
                return res.ok ? await res.json() : null;
            } catch (e) { return null; }
        }

        function buildOptions(select, data, selectedId, arrowId) {
            const arrow = document.getElementById(arrowId);
            select.innerHTML = '<option value="">選択してください</option>';
            
            if (!data || data.length === 0) {
                select.style.display = 'none';
                if(arrow) arrow.style.display = 'none';
                return;
            }
            
            data.forEach(c => {
                const opt = document.createElement('option');
                opt.value = c.id;
                opt.text = c.name;
                if (c.id == selectedId) opt.selected = true;
                select.appendChild(opt);
            });
            
            select.style.display = 'inline-block';
            if(arrow) arrow.style.display = 'inline-block';
        }

        async function completeRestore() {
            const urlParams = new URLSearchParams(window.location.search);
            const categoryId = urlParams.get('categoryId');
            if (!categoryId) return;

            for (let pOpt of parentSelect.options) {
                if (!pOpt.value) continue;
                const children = await getData(`/items/categories/${pOpt.value}/children`);
                if (!children) continue;

                if (pOpt.value == categoryId) {
                    parentSelect.value = categoryId;
                    buildOptions(childSelect, children, null, 'childArrow');
                    return;
                }

                const matchedChild = children.find(c => c.id == categoryId);
                if (matchedChild) {
                    parentSelect.value = pOpt.value;
                    buildOptions(childSelect, children, categoryId, 'childArrow');
                    const grandChildren = await getData(`/items/categories/${categoryId}/children`);
                    buildOptions(grandChildSelect, grandChildren, null, 'grandChildArrow');
                    return;
                }

                for (let child of children) {
                    const grandChildren = await getData(`/items/categories/${child.id}/children`);
                    if (!grandChildren) continue;
                    const matchedGrand = grandChildren.find(g => g.id == categoryId);
                    if (matchedGrand) {
                        parentSelect.value = pOpt.value;
                        buildOptions(childSelect, children, child.id, 'childArrow');
                        buildOptions(grandChildSelect, grandChildren, categoryId, 'grandChildArrow');
                        return;
                    }
                }
            }
        }

        function onCategoryChange() {
            form.submit();
        }

        parentSelect.addEventListener('change', e => {
            finalInput.value = e.target.value;
            onCategoryChange();
        });
        childSelect.addEventListener('change', e => {
            finalInput.value = e.target.value || parentSelect.value;
            onCategoryChange();
        });
        grandChildSelect.addEventListener('change', e => {
            finalInput.value = e.target.value || childSelect.value;
            onCategoryChange();
        });

        await completeRestore();
    }
});

document.addEventListener('DOMContentLoaded', function () {
    const includeSoldCheckbox = document.getElementById('includeSoldCheckbox');
    if (includeSoldCheckbox) {
        includeSoldCheckbox.addEventListener('change', function () {
            this.closest('form').submit();
        });
    }

    const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

    const favoriteIcons = document.querySelectorAll('.favorite-icon-container');

    favoriteIcons.forEach(iconContainer => {
        iconContainer.addEventListener('click', function () {
            const itemId = this.dataset.itemId;
            if (!itemId) return;

            fetch(`/api/favorites/toggle/${itemId}`, {
                method: 'POST',
                headers: {
                    [csrfHeader]: csrfToken,
                },
            })
            .then(response => {
                if (response.status === 401) {
                    window.location.href = '/login';
                    return;
                }
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                return response.json();
            })
            .then(data => {
                if (data) {
                    const icon = this.querySelector('.fa-heart');
                    if (data.favorited) {
                        this.classList.add('is-favorited');
                        icon.classList.remove('far');
                        icon.classList.add('fas');
                    } else {
                        this.classList.remove('is-favorited');
                        icon.classList.remove('fas');
                        icon.classList.add('far');
                    }
                }
            })
            .catch(error => {
                console.error('There was a problem with the fetch operation:', error);
            });
        });
    });
});