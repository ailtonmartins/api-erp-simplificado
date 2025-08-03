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

def extract_jacoco_counters(xml_path):
    counters = {
        'INSTRUCTION': {'missed': 0, 'covered': 0},
        'BRANCH': {'missed': 0, 'covered': 0},
        'LINE': {'missed': 0, 'covered': 0},
        'COMPLEXITY': {'missed': 0, 'covered': 0},
        'METHOD': {'missed': 0, 'covered': 0},
        'CLASS': {'missed': 0, 'covered': 0},
    }
    try:
        tree = ET.parse(xml_path)
        root = tree.getroot()
        for counter in root.findall('counter'):
            ctype = counter.attrib['type']
            if ctype in counters:
                counters[ctype]['missed'] = int(counter.attrib['missed'])
                counters[ctype]['covered'] = int(counter.attrib['covered'])
    except Exception as e:
        print(f"Erro ao ler cobertura do Jacoco: {e}")
    return counters

def percent(missed, covered):
    total = missed + covered
    return round((covered / total) * 100, 2) if total > 0 else 0.0

def generate_coverage_table(counters):
    table = [
        '<!-- cobertura-jacoco-start -->',
        '| Tipo        | Cobertura | Coberto | Não Coberto |',
        '|-------------|-----------|---------|-------------|',
    ]
    for ctype, label in [
        ('INSTRUCTION', 'Instruções'),
        ('BRANCH', 'Branches'),
        ('LINE', 'Linhas'),
        ('COMPLEXITY', 'Complexidade'),
        ('METHOD', 'Métodos'),
        ('CLASS', 'Classes'),
    ]:
        missed = counters[ctype]['missed']
        covered = counters[ctype]['covered']
        perc = percent(missed, covered)
        table.append(f"| {label:<11} | {perc}% | {covered} | {missed} |")
    table.append('<!-- cobertura-jacoco-end -->')
    return '\n'.join(table)

def insert_coverage_table_after_progress(content, table_md):
    # Procura a seção de progresso e insere a tabela logo após
    pattern = r'(## \uD83D\uDCCA Progresso.*?%)(\n)'
    match = re.search(pattern, content, re.DOTALL)
    if match:
        insert_pos = match.end(1)
        return content[:insert_pos] + '\n' + table_md + content[insert_pos:]
    # fallback: adiciona ao final
    return content + '\n' + table_md

def remove_old_coverage_table(content):
    # Remove tabela antiga se existir
    return re.sub(r'<!-- cobertura-jacoco-start -->(.|\n)*?<!-- cobertura-jacoco-end -->', '', content)

def main():
    with open(README_FILE, 'r', encoding='utf-8') as f:
        content = f.read()

    progress_percent = calculate_progress(content)
    progress_bar = generate_progress_bar(progress_percent)
    updated_content = update_readme(content, progress_percent, progress_bar)

    counters = extract_jacoco_counters(JACOCO_XML)
    table_md = generate_coverage_table(counters)
    updated_content = remove_old_coverage_table(updated_content)
    updated_content = insert_coverage_table_after_progress(updated_content, table_md)

    if content != updated_content:
        with open(README_FILE, 'w', encoding='utf-8') as f:
            f.write(updated_content)
        print('README.md atualizado com tabela de cobertura Jacoco.')
    else:
        print('Nenhuma alteração necessária.')

if __name__ == "__main__":
    main()