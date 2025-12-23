const imageInput = document.getElementById('imageInput');
    const imagePreview = document.getElementById('imagePreview');
    const newPreviewContainer = document.getElementById('new-preview-container');
    const existingPreview = document.getElementById('existing-preview');

    imageInput.addEventListener('change', function(e) {
        const file = e.target.files[0];
        
        if (file) {
            const reader = new FileReader();
            
            reader.onload = function(e) {
                // imgタグに画像ソースをセット
                imagePreview.src = e.target.result;
                // 新しいプレビューを表示
                newPreviewContainer.style.display = 'block';
                
                // もし編集用の「既存画像」があれば非表示にする（混乱を防ぐため）
                if (existingPreview) {
                    existingPreview.style.display = 'none';
                }
            }
            
            reader.readAsDataURL(file);
        } else {
            // ファイルが選択解除された場合
            newPreviewContainer.style.display = 'none';
            if (existingPreview) {
                existingPreview.style.display = 'block';
            }
        }
    });