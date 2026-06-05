import os
import re

dir_path = r'src\view'
for root, dirs, files in os.walk(dir_path):
    for file in files:
        if file.endswith('.java'):
            path = os.path.join(root, file)
            with open(path, 'r', encoding='utf-8') as f:
                content = f.read()

            new_content = content
            new_content = re.sub(r'new Color\(248,\s*250,\s*252\)', r'util.TechStoreUI.BG_MAIN', new_content)
            new_content = re.sub(r'new Color\(241,\s*245,\s*249\)', r'util.TechStoreUI.BG_MAIN', new_content)
            new_content = re.sub(r'new Color\(226,\s*232,\s*240\)', r'util.TechStoreUI.BORDER', new_content)

            if new_content != content:
                with open(path, 'w', encoding='utf-8') as f:
                    f.write(new_content)
                print(f"Updated {path}")
