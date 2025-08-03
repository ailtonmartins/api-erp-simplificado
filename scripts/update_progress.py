import re
import xml.etree.ElementTree as ET

README_FILE = 'README.md'
JACOCO_XML = 'test/jacocoTestReport.xml'
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

def extract_jacoco_coverage(xml_path):
    try:
        tree = ET.parse(xml_path)
        root = tree.getroot()
        counter = root.find("counter[@type='INSTRUCTION']")
        if counter is not None:
            covered = int(counter.attrib['covered'])
            missed = int(counter.attrib['missed'])
            total = covered + missed
            percent = (covered / total) * 100 if total > 0 else 0
            return round(percent, 2)
    except Exception as e:
        print(f"Erro ao ler cobertura do Jacoco: {e}")
    return None

def update_readme_coverage(content, coverage):
    # Atualiza ou insere a tabela de cobertura no README
    table_pattern = r'<!-- cobertura-jacoco-start -->(.|\n)*?<!-- cobertura-jacoco-end -->'
    table_md = f"""
<!-- cobertura-jacoco-start -->
| Tipo        | Cobertura |
|-------------|-----------|
| Instruções  | {coverage}% |
<!-- cobertura-jacoco-end -->
"""
    if re.search(table_pattern, content):
        return re.sub(table_pattern, table_md, content)
    else:
        return content + '\n' + table_md

def main():
    with open(README_FILE, 'r', encoding='utf-8') as f:
        content = f.read()

    progress_percent = calculate_progress(content)
    progress_bar = generate_progress_bar(progress_percent)
    updated_content = update_readme(content, progress_percent, progress_bar)

    coverage = extract_jacoco_coverage(JACOCO_XML)
    if coverage is not None:
        updated_content = update_readme_coverage(updated_content, coverage)
        print(f'Cobertura Jacoco: {coverage}%')
    else:
        print('Cobertura Jacoco não encontrada.')

    if content != updated_content:
        with open(README_FILE, 'w', encoding='utf-8') as f:
            f.write(updated_content)
        print(f'Updated README.md')
    else:
        print('No changes needed.')

if __name__ == "__main__":
    main()