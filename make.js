const fs = require('fs');
const path = require('path');

const baseDir = 'src/main/java/com/example/be12fin5verdosewmthisbe/menu_management/category';
const packagePath = baseDir.replace('src/main/java/', '');
const packageName = packagePath.replace(/\//g, '.');
const className = 'Category';

const packages = [
    {
        dir: 'controller',
        suffix: 'Controller',
        annotation: '@RestController',
        import: 'import org.springframework.web.bind.annotation.RestController;'
    },
    {
        dir: 'service',
        suffix: 'Service',
        annotation: '@Service',
        import: 'import org.springframework.stereotype.Service;'
    },
    {
        dir: 'repository',
        suffix: 'Repository',
        annotation: '@Repository',
        import: 'import org.springframework.stereotype.Repository;'
    },
    {
        dir: 'model',
        suffix: '',
        annotation: '@Entity',
        import: `import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;`,
        isEntity: true
    },
    {
        dir: 'model/dto',
        suffix: 'Dto',
        annotation: '',
        import: ''
    }
];

function createDirectories(basePath, structure) {
    structure.forEach(pkg => {
        const packageDir = path.join(basePath, pkg.dir.replace(/\//g, path.sep));
        if (!fs.existsSync(packageDir)) {
            fs.mkdirSync(packageDir, { recursive: true });
            console.log(`Directory created: ${packageDir}`);
        }

        const classNameWithSuffix = `${className}${pkg.suffix}`;
        const filePath = path.join(packageDir, `${classNameWithSuffix}.java`);
        const packageFull = packageName + `.${pkg.dir.replace(/\//g, '.')}`;

        // 엔티티 내용 포함
        let body = '';
        if (pkg.isEntity) {
            body = `
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
            `.trim();
        } else {
            body = '    // Your code here';
        }

        const content = `
package ${packageFull};

${pkg.import || ''}

${pkg.annotation || ''}
public class ${classNameWithSuffix} {
${body}
}
        `.trimStart();

        fs.writeFileSync(filePath, content);
        console.log(`File created: ${filePath}`);
    });
}

createDirectories(baseDir, packages);
