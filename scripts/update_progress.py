import re

README_FILE = 'README.md'
PROGRESS_BAR_LENGTH = 10  # Número total de blocos (🟩⬜)

def calculate_progress(content):
    total_items = len(re.findall(r'- \[.\]', content))
    checked_items = len(re.findall(r'- \[x\]', content, re.IGNORECASE))
    if total_items == 0:
        return 0
    progress = int((checked_items / total_items) * 100)
    return progress

def generate_progress_bar(progress_percent):
    filled_blocks = int((progress_percent / 100) * PROGRESS_BAR_LENGTH)
    empty_blocks = PROGRESS_BAR_LENGTH - filled_blocks
    return '🟩' * filled_blocks + '⬜' * empty_blocks

def update_readme(content, progress_percent, progress_bar):
    # Atualizar a barra de emojis + porcentagem no README
    updated_content = re.sub(
        r'(🟩|⬜){1,}\s\d+%',
        f'{progress_bar} {progress_percent}%',
        content
    )
    return updated_content

def main():
    with open(README_FILE, 'r', encoding='utf-8') as f:
        content = f.read()

    progress_percent = calculate_progress(content)
    progress_bar = generate_progress_bar(progress_percent)
    updated_content = update_readme(content, progress_percent, progress_bar)

    if content != updated_content:
        with open(README_FILE, 'w', encoding='utf-8') as f:
            f.write(updated_content)
        print(f'Updated progress bar to {progress_percent}%')
    else:
        print('No changes needed.')

if __name__ == "__main__":
    main()